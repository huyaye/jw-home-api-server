package com.jw.home.rest.handler;

import com.jw.home.domain.mapper.HomeMapper;
import com.jw.home.rest.AuthInfoManager;
import com.jw.home.rest.dto.*;
import com.jw.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class HomeHandler {

    @Autowired
    private HomeService homeService;

    public Mono<ServerResponse> createHome(ServerRequest request) {
        Mono<String> memId = AuthInfoManager.getRequestMemId();
        return request.bodyToMono(AddHomeReq.class)
                .map(HomeMapper.INSTANCE::toHome)
                .flatMap(home -> homeService.addHome(memId, home))
                .map(HomeMapper.INSTANCE::toAddHomeRes)
                .flatMap(res -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, res)));
    }

    public Mono<ServerResponse> getHomes(ServerRequest request) {
        Mono<String> memId = AuthInfoManager.getRequestMemId();
        return homeService.getHomes(memId)
                .collectList()
                .flatMap(homes -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, new GetHomesRes(homes))));
    }

    public Mono<ServerResponse> withdrawHomes(ServerRequest request) {
        Mono<String> memId = AuthInfoManager.getRequestMemId();
        return request.bodyToMono(DeleteHomesReqRes.class)
                .flatMapMany(req -> homeService.withdrawHomes(memId, req.getHomeIds()))
                .collectList()
                .flatMap(deletedIds -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, new DeleteHomesReqRes(deletedIds))));
    }

    public Mono<ServerResponse> inviteHome(ServerRequest request) {
        Mono<String> hostMemberId = AuthInfoManager.getRequestMemId();
        return request.bodyToMono(InviteHomeReq.class)
                .flatMap(req -> homeService.inviteHome(hostMemberId, req))
                .flatMap(home -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, Collections.emptyMap())));
    }
}
