package com.jw.home.service.device;

import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.exception.DeviceControlException;
import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.rest.dto.ControlDeviceRes;
import com.jw.home.rest.dto.ControlDeviceStatus;
import com.jw.home.service.device.mapper.DeviceServerDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DeviceServerCaller {
    @Value(value = "${device.websocket.server.uri}")
    private String webSocketServerUri;
    @Autowired
    private WebClient webClient;

    public Mono<ControlDeviceRes> controlDevice(ControlDeviceReq controlData, String deviceSerial) {
        return webClient.mutate().build()
                .put()
                .uri(getUrl(controlData.getConnection()) + "/api/v1/devices/control")
                .header("TRANSACTION_ID", MDC.get("TRACE_ID"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(DeviceServerDtoMapper.INSTANCE.toControlDeviceReq(controlData, deviceSerial)),
                        com.jw.home.service.device.dto.ControlDeviceReq.class)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.warn("failed to call {} device server : {}", controlData.getConnection(), clientResponse.statusCode());
                    ControlDeviceRes cause = new ControlDeviceRes();
                    cause.setStatus(ControlDeviceStatus.ERROR);
                    cause.setCause("deviceOffline");
                    return Mono.just(new DeviceControlException(cause));
                })
                .bodyToMono(ControlDeviceRes.class);
    }

    private String getUrl(DeviceConnection connection) {
        switch (connection) {
            case websocket:
                return webSocketServerUri;
            case mqtt:
            default:
                return null;
        }
    }
}
