package com.jw.home.common.log;

import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

public class HttpResponseLogDecorator extends ServerHttpResponseDecorator {
  private static final Logger log = LoggerFactory.getLogger("HTTP");

  private String url;

  public HttpResponseLogDecorator(final ServerWebExchange exchange) {
    super(exchange.getResponse());
    url = exchange.getRequest().getURI().getPath();
    exchange.getResponse().beforeCommit(() -> {
      log.info("[" + getRawStatusCode() + "] " + url);
      return Mono.empty();
    });
  }

  public HttpResponseLogDecorator(final ServerHttpResponse response) {
    super(response);
  }

  @Override
  public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
    Flux<DataBuffer> buffer = Flux.from(body);
    return super.writeWith(buffer.doOnNext(dataBuffer -> {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
        String resBody = IOUtils.toString(baos.toByteArray(), "UTF-8");
        log.info("Response - " + resBody);
      } catch (IOException e) {
        log.error("exception = ", e);
      }
    }));
  }
}