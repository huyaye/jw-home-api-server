package com.jw.home.rest.handler;

import com.jw.home.domain.Home;
import com.jw.home.exception.HomeLimitException;
import com.jw.home.rest.dto.AddHomeDto;
import com.jw.home.rest.dto.ResponseDto;
import com.jw.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
public class HomeHandler {

    @Autowired
    private HomeService homeService;

    public Mono<ServerResponse> createHome(ServerRequest request) {
        return request.bodyToMono(AddHomeDto.class)
                .map(param -> {
                    Home home = new Home();
                    home.setHomeName(param.getHomeName());
                    home.setTimezone(param.getTimezone());
                    home.setSecurityMode(param.getSecurityMode());
                    home.setRooms(param.getRooms().stream()
                            .map(s -> {
                                Home.Room room = new Home.Room();
                                room.setRoomName(s.getRoomName());
                                return room;
                            }).collect(Collectors.toList()));
                    return home;
                })
                .flatMap(home -> homeService.addHome("jwryu", home))
                .flatMap(home -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto(null, home)))
                .onErrorResume(err -> {
                            ServerResponse.BodyBuilder builder = ServerResponse.status(HttpStatus.CONFLICT)
                                    .contentType(MediaType.APPLICATION_JSON);
                            if (err instanceof HomeLimitException) {
                                return builder.body(Mono.just(new ResponseDto(302, null)), ResponseDto.class);
                            }
                            return builder.build();
                        }
                );
    }
}
