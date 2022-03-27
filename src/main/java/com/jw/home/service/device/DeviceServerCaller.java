package com.jw.home.service.device;

import com.jw.home.common.spec.DeviceConnection;
import com.jw.home.exception.DeviceControlException;
import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.rest.dto.ControlDeviceRes;
import com.jw.home.rest.dto.ControlDeviceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public Mono<ControlDeviceRes> controlDevice(ControlDeviceReq controlData) {
        return webClient.mutate().build()
                .put()
                .uri(getUrl(controlData.getConnection()) + "/api/v1/devices/control")
//                .header("Bearer", accessToken)    // TODO accessToken
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(controlData), ControlDeviceReq.class)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse -> {
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
