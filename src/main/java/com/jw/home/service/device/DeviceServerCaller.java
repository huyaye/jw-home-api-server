package com.jw.home.service.device;

import com.jw.home.exception.DeviceControlException;
import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.rest.dto.ControlDeviceRes;
import com.jw.home.rest.dto.ControlDeviceStatus;
import com.jw.home.service.device.dto.ControlReqMsg;
import com.jw.home.service.device.dto.ControlResMsg;
import com.jw.home.service.device.mapper.DeviceServerDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Slf4j
public class DeviceServerCaller {
    @Value(value = "${spring.redis.pubsub.device.control-request-channel-postfix}")
    private String controlRequestChannelPostfix;
    @Value(value = "${spring.redis.pubsub.device.control-response-channel-postfix}")
    private String controlResponseChannelPostfix;
    @Value(value = "${spring.redis.pubsub.device.control-block-seconds}")
    private Integer controlBlockSeconds;

    @Autowired
    private ReactiveRedisOperations<String, ControlReqMsg> controlReqTemplate;
    @Autowired
    private ReactiveRedisOperations<String, ControlResMsg> controlResTemplate;

    public Mono<ControlDeviceRes> controlDevice(ControlDeviceReq controlData, String deviceSerial) {
        String transactionId = MDC.get("TRACE_ID");
        String requestChannel = controlData.getConnection() + controlRequestChannelPostfix;
        String responseChannel = controlData.getConnection() + controlResponseChannelPostfix;
        ControlReqMsg controlReqMsg = DeviceServerDtoMapper.INSTANCE.toControlReqMsg(controlData, transactionId, deviceSerial);

        // Redis pub/sub을 사용한 동기 호출
        return controlReqTemplate.convertAndSend(requestChannel, controlReqMsg)
                .flatMapMany(cnt -> controlResTemplate.listenTo(ChannelTopic.of(responseChannel)))
                .map(ReactiveSubscription.Message::getMessage)
                .doOnNext(msg -> log.debug("subscribe control res : {}", msg))
                .filter(msg -> msg.getTransactionId().equals(transactionId))
                .timeout(Duration.ofSeconds(controlBlockSeconds))    // 최대 설정초까지 동일한 transaction_id 메세지 수신 대기
                .onErrorResume(e -> Mono.error(createDeviceControlException()))
                .elementAt(0)
                .map(DeviceServerDtoMapper.INSTANCE::toControlDeviceRes);
    }

    @Bean
    private DeviceControlException createDeviceControlException() {
        ControlDeviceRes errorRes = new ControlDeviceRes();
        errorRes.setStatus(ControlDeviceStatus.ERROR);
        errorRes.setCause("serverOffline");
        return new DeviceControlException(errorRes);
    }

    @Bean
    private ReactiveRedisOperations<String, ControlReqMsg> controlReqTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisSerializer<ControlReqMsg> valueSerializer = new Jackson2JsonRedisSerializer<>(ControlReqMsg.class);
        RedisSerializationContext<String, ControlReqMsg> serializationContext = RedisSerializationContext.<String, ControlReqMsg>newSerializationContext(RedisSerializer.string())
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(lettuceConnectionFactory, serializationContext);
    }

    @Bean
    private ReactiveRedisOperations<String, ControlResMsg> controlResTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisSerializer<ControlResMsg> valueSerializer = new Jackson2JsonRedisSerializer<>(ControlResMsg.class);
        RedisSerializationContext<String, ControlResMsg> serializationContext = RedisSerializationContext.<String, ControlResMsg>newSerializationContext(RedisSerializer.string())
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(lettuceConnectionFactory, serializationContext);
    }
}