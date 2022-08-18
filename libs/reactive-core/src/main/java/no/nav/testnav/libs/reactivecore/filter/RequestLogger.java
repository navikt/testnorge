package no.nav.testnav.libs.reactivecore.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(1)
public class RequestLogger implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!exchange.getRequest().getPath().toString().contains("/api")) {
            return chain.filter(exchange);
        }

        var logServerHttpRequestDecorator = new LogServerHttpRequestDecorator(exchange.getRequest(), logRequest());
        var logServerHttpResponseDecorator = new LogServerHttpResponseDecorator(exchange.getResponse(), exchange.getRequest(), logResponse());

        return chain.filter(new ResponseAndRequestServerWebExchangeDecorator(exchange, logServerHttpRequestDecorator, logServerHttpResponseDecorator));
    }

    private LogRequest logRequest() {
        return (request, body) -> {
            if (!log.isTraceEnabled()) {
                return;
            }
            Map<String, String> original = MDC.getCopyOfContextMap();
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            var method = request.getMethod().name();
            var uri = request.getPath().toString();
            var queryParrams = request.getQueryParams().isEmpty() ? null : request.getQueryParams().toString();
            var host = request.getHeaders().getHost().toString();
            var contentType = request.getHeaders().getContentType();

            contextMap.put("Transaction-Type", "request");
            contextMap.put("Method", method);
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
            MDC.setContextMap(original);

        };
    }


    private LogResponse logResponse() {
        return (response, request, body) -> {
            if (!log.isTraceEnabled()) {
                return;
            }
            Map<String, String> original = MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap() : new HashMap<>();
            Map<String, String> contextMap = MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap() : new HashMap<>();
            var method = request.getMethod().name();
            var uri = request.getPath().toString();
            var statusCode = response.getRawStatusCode();
            var queryParrams = request.getQueryParams().isEmpty() ? null : request.getQueryParams().toString();
            var host = request.getHeaders().getHost().toString();

            contextMap.put("Transaction-Type", "response");
            contextMap.put("Method", method);
            contextMap.put(HttpHeaders.CONTENT_TYPE, response.getHeaders().getContentType().getType());
            contextMap.put(HttpHeaders.HOST, host);
            contextMap.put(HttpHeaders.ORIGIN, response.getHeaders().getOrigin());
            contextMap.put(HttpHeaders.ACCEPT, response.getHeaders().getAccept().toString());
            contextMap.put("Query-Params", queryParrams);
            if (!response.getStatusCode().is2xxSuccessful()) {
                contextMap.put("Body", body == null ? "[empty]" : body);
            }
            contextMap.put("Http-Status", statusCode.toString());
            contextMap.put("URI", uri);

            MDC.setContextMap(contextMap);
            log.trace("[Response] {} {}{} HTTP:{}", method, host, uri, statusCode);
            MDC.setContextMap(original);
        };
    }
}
