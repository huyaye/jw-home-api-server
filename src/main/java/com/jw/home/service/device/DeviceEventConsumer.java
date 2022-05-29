package com.jw.home.service.device;

import com.jw.home.kafka.dto.DeviceStateValue;
import com.jw.home.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;

import java.util.Map;

@Slf4j
@Component
public class DeviceEventConsumer {
    private final KafkaReceiver<String, DeviceStateValue> kafkaReceiver;

    private final DeviceRepository deviceRepository;

    public DeviceEventConsumer(KafkaReceiver<String, DeviceStateValue> kafkaReceiver, DeviceRepository deviceRepository) {
        this.kafkaReceiver = kafkaReceiver;
        this.deviceRepository = deviceRepository;

        startConsume();
    }

    private void startConsume() {
        log.info("Start consume kafka topic >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        kafkaReceiver.receive().subscribe(record -> {
            log.info("Kafka message receive : {}", record);
            String deviceId = record.key();
            DeviceStateValue deviceState = record.value();
            deviceRepository.findById(deviceId)
                    .flatMap(device -> {
                        Map<String, Object> states = deviceState.getStates();
                        Boolean online = deviceState.getOnline();
                        if (states != null) {
                            device.updateState(states);
                        }
                        if (online != null) {
                            device.setOnline(online);
                        }
                        return deviceRepository.save(device);
                    }).subscribe();

            record.receiverOffset().acknowledge();  // offset commit
        });
    }
}
