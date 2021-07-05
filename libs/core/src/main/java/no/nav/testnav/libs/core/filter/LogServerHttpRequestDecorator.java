package no.nav.testnav.libs.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Waring: ikke bruk der tokens er inkludert i body.
 */
@Slf4j
public class LogServerHttpRequestDecorator extends ServerHttpRequestDecorator {

    public LogServerHttpRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(dataBuffer -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                logRequest(getDelegate(), byteArrayOutputStream.toString(StandardCharsets.UTF_8));
            } catch (Exception exception) {
                log.error("Feil med logging av requests.", exception);
            }
        });
    }

    private void logRequest(ServerHttpRequest request, String body) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap() != null ? MDC.getCopyOfContextMap() : new HashMap<>();
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
        contextMap.put("Body", body == null ? "[empty]" : body);
        contextMap.put("URI", uri);

        MDC.setContextMap(contextMap);
        log.trace("[Request ] {} {}{}", method, host, uri);

    }
}
