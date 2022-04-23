package com.jw.home.rest.handler;

import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.domain.mapper.DeviceMapper;
import com.jw.home.rest.AuthInfoManager;
import com.jw.home.rest.annotation.QueryParam;
import com.jw.home.rest.dto.*;
import com.jw.home.rest.validator.GetDevicesReqValidator;
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

    public Mono<ServerResponse> controlDevice(ServerRequest request) {
        Mono<String> memId = AuthInfoManager.getRequestMemId();
        return request.bodyToMono(ControlDeviceReq.class)
                .doOnNext(req -> log.debug(req.toString()))
                .flatMap(req -> deviceService.controlDevice(memId, req))
                .flatMap(res -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, res)));
    }

    @QueryParam(validator = GetDevicesReqValidator.class)
    public Mono<ServerResponse> getDevices(ServerRequest request) {
        DeviceConnection connection = DeviceConnection.valueOf(request.queryParam("connection").get());
        String serial = request.queryParam("serial").get();
        return deviceService.getDevices(connection, serial)
                .map(DeviceMapper.INSTANCE::toDeviceDto)
                .collectList()
                .flatMap(devices -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, new GetDevicesRes(devices))));
    }

    public Mono<ServerResponse> deleteDevices(ServerRequest request) {
        Mono<String> memId = AuthInfoManager.getRequestMemId();
        return request.bodyToMono(DeleteDevicesReqRes.class)
                .flatMapMany(req -> deviceService.deleteDevices(memId, req.getDeviceIds()))
                .collectList()
                .flatMap(deletedIds -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, new DeleteDevicesReqRes(deletedIds))));
    }
}
