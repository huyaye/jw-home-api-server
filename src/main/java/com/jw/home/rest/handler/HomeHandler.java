package com.jw.home.rest.handler;

import com.jw.home.domain.Home;
import com.jw.home.rest.AuthInfoManager;
import com.jw.home.rest.dto.AddHomeDto;
import com.jw.home.rest.dto.ResponseDto;
import com.jw.home.service.HomeService;
import com.jw.home.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MemberService memberService;

    public Mono<ServerResponse> createHome(ServerRequest request) {
        Mono<String> memId = AuthInfoManager.getRequestMemId();
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
                .flatMap(home -> homeService.addHome(memId, home))
                .flatMap(home -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto(null, home)));
    }
}
