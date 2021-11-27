package com.jw.home.rest;

import com.jw.home.exception.CustomBusinessException;
import com.jw.home.rest.dto.ResponseDto;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Component
public class RestExceptionHandler extends AbstractErrorWebExceptionHandler {

    public RestExceptionHandler(final ErrorAttributes g, final ApplicationContext applicationContext,
                                final ServerCodecConfigurer serverCodecConfigurer) {
        super(g, new ResourceProperties(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.path("/api/v1/homes"), this::handleErrorResponse);
    }

    private Mono<ServerResponse> handleErrorResponse(final ServerRequest request) {
        Throwable throwable = this.getError(request);

        if (throwable instanceof CustomBusinessException) {
            final Integer resultCode = ((CustomBusinessException) throwable).getResultCode();
            return ServerResponse.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(new ResponseDto(resultCode, null)), ResponseDto.class);
        }

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(throwable.getMessage());
    }
}
