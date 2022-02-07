package com.jw.home.common.mdc;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import org.springframework.stereotype.Component;
import reactor.util.context.Context;

import java.util.UUID;

@Component
public class MdcHttpHandlerDecorator implements HttpHandlerDecoratorFactory {
    private static final String MDC_KEY_TRACE_ID = "TRACE_ID";

    @Override
    public HttpHandler apply(HttpHandler httpHandler) {
        return (request, response) -> httpHandler
                .handle(request, response)
                .contextWrite(context -> {
                    final String traceId = getTraceId();
                    MDC.put(MDC_KEY_TRACE_ID, traceId);
                    return Context.of(MDC_KEY_TRACE_ID, traceId);
                });
    }

    private String getTraceId() {
        return UUID.randomUUID().toString();
    }
}

