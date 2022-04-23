package com.jw.home.rest.router;

import com.jw.home.rest.handler.DeviceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class DeviceRouter {
    @Bean
    public RouterFunction<ServerResponse> deviceRoute(DeviceHandler deviceHandler) {
        return RouterFunctions.route()
                .GET("/api/v1/admin/devices", deviceHandler::getDevices)
                .POST("/api/v1/devices", deviceHandler::addDevice)
                .PUT("/api/v1/devices/control", deviceHandler::controlDevice)
                .DELETE("/api/v1/devices", deviceHandler::deleteDevices)
                .build();
    }
}
