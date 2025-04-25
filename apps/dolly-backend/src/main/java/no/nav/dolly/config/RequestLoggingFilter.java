package no.nav.dolly.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter implements WebFilter {
  
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    String traceId = UUID.randomUUID().toString();
    MDC.put("traceId", traceId);
    chain.doFilter(request, response);
  }

  @Override
  public Mono<Void> filter(ServletRequest request, ServletResponse response, FilterChain chain) {
    String traceId = UUID.randomUUID().toString();
    MDC.put("traceId", traceId);
    chain.doFilter(request, response);
  }
}