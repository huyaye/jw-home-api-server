package com.jw.home.rest.router;

import com.jw.home.rest.handler.DeviceHandler;
import com.jw.home.rest.handler.HomeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.logging.Handler;

@Configuration
public class DeviceRouter {
    @Bean
    public RouterFunction<ServerResponse> deviceRoute(DeviceHandler deviceHandler) {
        return RouterFunctions.route()
                .POST("/api/v1/devices", deviceHandler::addDevice)
                .build();
    }
}
