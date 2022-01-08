package com.jw.home.rest.router;

import com.jw.home.rest.handler.HomeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HomeRouter {
    @Bean
    public RouterFunction<ServerResponse> homeRoute(HomeHandler homeHandler) {
        return RouterFunctions.route()
                .POST("/api/v1/homes", homeHandler::createHome)
                .GET("/api/v1/homes", homeHandler::getHomes)
                .DELETE("/api/v1/homes", homeHandler::withdrawHomes)
                .POST("/api/v1/homes/invite", homeHandler::inviteHome)
                .build();
    }

}
