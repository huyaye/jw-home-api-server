package com.jw.home.rest.handler;

import com.jw.home.domain.mapper.DeviceMapper;
import com.jw.home.rest.dto.AddDeviceReq;
import com.jw.home.rest.dto.ResponseDto;
import com.jw.home.service.device.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Component
public class DeviceHandler {
    @Autowired
    private DeviceService deviceService;

    public Mono<ServerResponse> addDevice(ServerRequest request) {
        return request.bodyToMono(AddDeviceReq.class)
                .doOnNext(req -> log.debug(req.toString()))
                .flatMap(req -> deviceService.addDevice(req.getUserId(), DeviceMapper.INSTANCE.toDevice(req)))
                .flatMap(deviceId -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, Collections.singletonMap("id", deviceId))));
    }
}
