package com.jw.home.rest.handler;

import com.jw.home.domain.mapper.HomeMapper;
import com.jw.home.rest.AuthInfoManager;
import com.jw.home.rest.dto.AddHomeReq;
import com.jw.home.rest.dto.DeleteHomesReqRes;
import com.jw.home.rest.dto.GetHomesRes;
import com.jw.home.rest.dto.ResponseDto;
import com.jw.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
                .map(HomeMapper.INSTANCE::toGetHomesHomeDto)
                .collectList()
                .flatMap(homes -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, new GetHomesRes(homes))));
    }

    public Mono<ServerResponse> deleteHomes(ServerRequest request) {
        Mono<String> memId = AuthInfoManager.getRequestMemId();
        return request.bodyToMono(DeleteHomesReqRes.class)
                .flatMapMany(req -> homeService.deleteHomes(memId, req.getHomeIds()))
                .collectList()
                .flatMap(deletedIds -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ResponseDto<>(null, null, new DeleteHomesReqRes(deletedIds))));
    }
}
