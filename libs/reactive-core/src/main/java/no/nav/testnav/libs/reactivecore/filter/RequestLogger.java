package no.nav.testnav.libs.reactivecore.filter;

import io.micrometer.common.lang.NonNullApi;
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

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(1)
@NonNullApi
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
            var originalContextMap = MDC.getCopyOfContextMap();
            var modifiedContextMap = Optional
                    .of(MDC.getCopyOfContextMap())
                    .orElse(new HashMap<>());
            MDC.setContextMap(new HashMap<>());
            var method = request.getMethod().name();
            var uri = request.getPath().toString();
            var queryParrams = request.getQueryParams().isEmpty() ? null : request.getQueryParams().toString();
            var host = Optional
                    .ofNullable(request.getHeaders().getHost())
                    .map(InetSocketAddress::toString)
                    .orElse("unknown");
            var contentType = request.getHeaders().getContentType();

            modifiedContextMap.put("Transaction-Type", "request");
            modifiedContextMap.put("Method", method);
            if (contentType != null) {
                modifiedContextMap.put(HttpHeaders.CONTENT_TYPE, contentType.getType());
            }
            modifiedContextMap.put(HttpHeaders.HOST, host);
            modifiedContextMap.put(HttpHeaders.ORIGIN, request.getHeaders().getOrigin());
            modifiedContextMap.put(HttpHeaders.ACCEPT, request.getHeaders().getAccept().toString());
            modifiedContextMap.put("Query-Params", queryParrams);
            modifiedContextMap.put("URI", uri);

            MDC.setContextMap(modifiedContextMap);
            log.trace("[Request ] {} {}{}", method, host, uri);
            MDC.setContextMap(originalContextMap);

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
            var queryParrams = request.getQueryParams().isEmpty() ? null : request.getQueryParams().toString();
            var host = Objects.requireNonNull(request.getHeaders().getHost()).toString();

            contextMap.put("Transaction-Type", "response");
            contextMap.put("Method", method);
            contextMap.put(HttpHeaders.CONTENT_TYPE, Objects.requireNonNull(response.getHeaders().getContentType()).getType());
            contextMap.put(HttpHeaders.HOST, host);
            contextMap.put(HttpHeaders.ORIGIN, response.getHeaders().getOrigin());
            contextMap.put(HttpHeaders.ACCEPT, response.getHeaders().getAccept().toString());
            contextMap.put("Query-Params", queryParrams);
            if (!Objects.requireNonNull(response.getStatusCode()).is2xxSuccessful()) {
                contextMap.put("Body", body == null ? "[empty]" : body);
            }

            var statusCode = response.getStatusCode();
            contextMap.put(
                    "Http-Status",
                    Optional
                            .ofNullable(statusCode)
                            .map(Object::toString)
                            .orElse("unknown"));
            contextMap.put("URI", uri);

            MDC.setContextMap(contextMap);
            log.trace("[Response] {} {}{} HTTP:{}", method, host, uri, statusCode);
            MDC.setContextMap(original);
        };
    }
}
