package com.jw.home.common.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
            .tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) //miliseconds
                                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(10))
                                    .addHandlerLast(new WriteTimeoutHandler(60))));
        //Memory 조정: 2M (default 256KB)
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configure -> configure.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter((req, next) -> next.exchange(ClientRequest.from(req).build()))
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        request -> {
                            log.info(">>>>>>>>>> REQUEST <<<<<<<<<<");
                            log.info("Request: {} {}", request.method(), request.url());
                            request.headers().forEach(
                                    (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
                            );
                            return Mono.just(request);
                        }
                    )
                )
                .filter(ExchangeFilterFunction.ofResponseProcessor(
                        response -> {
                            log.info(">>>>>>>>>> RESPONSE <<<<<<<<<<");
                            log.info("statusCode : {}", response.statusCode());
                            return Mono.just(response);
                        }
                    )
                )
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
