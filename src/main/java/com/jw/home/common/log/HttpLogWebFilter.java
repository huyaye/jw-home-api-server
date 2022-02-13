package com.jw.home.common.log;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(-1000)
public class HttpLogWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        return chain.filter(
                exchange.mutate()
                        .request(new HttpRequestLogDecorator(exchange.getRequest()))
                        .response(new HttpResponseLogDecorator(exchange))
                        .build());
    }
}
