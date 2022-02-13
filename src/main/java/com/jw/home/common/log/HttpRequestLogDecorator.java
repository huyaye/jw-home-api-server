package com.jw.home.common.log;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

public class HttpRequestLogDecorator extends ServerHttpRequestDecorator {
  private static final Logger log = LoggerFactory.getLogger("HTTP");

  public HttpRequestLogDecorator(final ServerHttpRequest delegate) {
    super(delegate);
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(getMethod()).append("] ")
            .append(getURI().getPath());
    String query = getURI().getRawQuery();
    if (query != null) {
      sb.append(getURI().getPath()).append("?")
              .append(getURI().getRawQuery());
    }
    log.info(sb.toString());
  }

  @Override
  public Flux<DataBuffer> getBody() {
    return super.getBody().doOnNext(dataBuffer -> {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
        String body = IOUtils.toString(baos.toByteArray(), "UTF-8");
        log.info("Request - " + body);
      } catch (IOException e) {
        log.error("exception = ", e);
      }
    });
  }
}
