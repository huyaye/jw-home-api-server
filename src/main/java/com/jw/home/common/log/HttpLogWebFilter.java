package com.jw.home.common.log;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Component
@Order(-1000)
public class HttpLogWebFilter implements WebFilter {
    private final PathPattern passPathPattern = new PathPatternParser().parse("/actuator/**");

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        if (passPathPattern.matches(exchange.getRequest().getPath().pathWithinApplication())) {
            return chain.filter(exchange);
        }
        return chain.filter(
                exchange.mutate()
                        .request(new HttpRequestLogDecorator(exchange.getRequest()))
                        .response(new HttpResponseLogDecorator(exchange))
                        .build());
    }
}
