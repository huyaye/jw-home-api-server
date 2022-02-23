package com.jw.home.rest.handler;

import com.jw.home.domain.mapper.DeviceMapper;
import com.jw.home.rest.AuthInfoManager;
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
        Mono<String> memId = AuthInfoManager.getRequestMemId();
        return request.bodyToMono(AddDeviceReq.class)
                .doOnNext(req -> log.debug(req.toString()))
                .map(DeviceMapper.INSTANCE::toDevice)
                .flatMap(device -> deviceService.addDevice(memId, device))
                .flatMap(deviceId -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, Collections.singletonMap("id", deviceId))));
    }
}
