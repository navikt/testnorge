package no.nav.dolly.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestLoggingFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

    if (!exchange.getRequest().getPath().toString().contains("/api")) {
      String traceId = UUID.randomUUID().toString();
      MDC.put("traceId", traceId);
    }
    return chain.filter(exchange);
  }
}