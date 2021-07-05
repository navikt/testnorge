package no.nav.testnav.libs.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Waring: Ikke bruk der tokens er inkludert i body.
 */
@Slf4j
public class LogServerHttpResponseDecorator extends ServerHttpResponseDecorator {
    private final ServerHttpRequest serverHttpRequest;

    public LogServerHttpResponseDecorator(ServerHttpResponse delegate, ServerHttpRequest request) {
        super(delegate);
        this.serverHttpRequest = request;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        Mono<DataBuffer> buffer = Mono.from(body);
        return super.writeWith(buffer.doOnNext(dataBuffer -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                logResponse(getDelegate(), serverHttpRequest, byteArrayOutputStream.toString(StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("Feil med logging av request.", e);
            }
        }));
    }

    private void logResponse(ServerHttpResponse response, ServerHttpRequest request, String body) {
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
        contextMap.put("Body", body == null ? "[empty]" : body);
        contextMap.put("Http-Status", statusCode.toString());
        contextMap.put("URI", uri);

        MDC.setContextMap(contextMap);
        log.info("Response - method:{} - HTTP:{} - host:{} - uri:{} - query:{}", method, statusCode, host, uri, queryParrams);
    }
}
