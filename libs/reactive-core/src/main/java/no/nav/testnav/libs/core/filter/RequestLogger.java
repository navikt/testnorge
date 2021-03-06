package no.nav.testnav.libs.core.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RequestLogger implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!exchange.getRequest().getPath().toString().contains("/api")) {
            return chain.filter(exchange);
        }
        logRequest(exchange.getRequest());

        exchange.getResponse().beforeCommit(() -> {
            logResponse(exchange.getResponse(), exchange.getRequest());
            return Mono.empty();
        });
        return chain.filter(exchange);
    }

    private void logRequest(ServerHttpRequest request) {
        if (!log.isTraceEnabled()) {
            return;
        }

        try {
            Map<String, String> contextMap = MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap() : new HashMap<>();
            var method = request.getMethod().name();
            var uri = request.getPath().toString();
            var queryParrams = request.getQueryParams().isEmpty() ? null : request.getQueryParams().toString();
            var host = request.getHeaders().getHost().toString();

            contextMap.put("Transaction-Type", "request");
            contextMap.put("Method", method);
            var contentType = request.getHeaders().getContentType();

            if (contentType != null) {
                contextMap.put(HttpHeaders.CONTENT_TYPE, contentType.getType());
            }
            contextMap.put(HttpHeaders.HOST, host);
            contextMap.put(HttpHeaders.ORIGIN, request.getHeaders().getOrigin());
            contextMap.put(HttpHeaders.ACCEPT, request.getHeaders().getAccept().toString());
            contextMap.put("Query-Params", queryParrams);
            contextMap.put("URI", uri);

            MDC.setContextMap(contextMap);
            log.trace("[Request ] {} {}{}", method, host, uri);
        } catch (Exception e) {
            log.error("Feil med logging av request.", e);
        }

    }


    private void logResponse(ServerHttpResponse response, ServerHttpRequest request) {
        if (!log.isTraceEnabled()) {
            return;
        }

        try {
            Map<String, String> contextMap = MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap() : new HashMap<>();

            var method = request.getMethod().name();
            var uri = request.getPath().toString();
            var statusCode = response.getRawStatusCode();
            var queryParrams = request.getQueryParams().isEmpty() ? null : request.getQueryParams().toString();
            var host = request.getHeaders().getHost().toString();
            var contentType = response.getHeaders().getContentType();

            contextMap.put("Transaction-Type", "response");

            contextMap.put("Method", method);

            if (contentType != null) {
                contextMap.put(HttpHeaders.CONTENT_TYPE, contentType.getType());
            }

            contextMap.put(HttpHeaders.HOST, host);
            contextMap.put(HttpHeaders.ORIGIN, response.getHeaders().getOrigin());
            contextMap.put(HttpHeaders.ACCEPT, response.getHeaders().getAccept().toString());
            contextMap.put("Query-Params", queryParrams);
            contextMap.put("Http-Status", statusCode.toString());
            contextMap.put("URI", uri);

            MDC.setContextMap(contextMap);
            log.trace("[Response] {} {}{} HTTP:{}", method, host, uri, statusCode);
        } catch (Exception e) {
            log.error("Feil med logging av response.", e);
        }

    }
}
