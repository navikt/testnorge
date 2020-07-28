package no.nav.registre.testnorge.common.log;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RequestLogger {

    private final ContentCachingRequestWrapper request;

    public RequestLogger(ContentCachingRequestWrapper request) {
        this.request = request;
    }

    private String getBody() throws IOException {
        return new String(request.getContentAsByteArray(), request.getCharacterEncoding());
    }

    public void log() {
        try {
            if (MDC.getCopyOfContextMap() != null) {
                Map<String, String> contextMap = MDC.getCopyOfContextMap();
                contextMap.putAll(this.toPropertyMap());
                MDC.setContextMap(contextMap);
            } else {
                MDC.setContextMap(this.toPropertyMap());
            }
            var body = getBody();
            log.trace(body.equals("") ? "[empty]" : body);
        } catch (Exception e) {
            log.error("Klarer ikke å lese fra request", e);
        }
    }

    private Map<String, String> toPropertyMap() {
        Map<String, String> properties = new HashMap<>();
        properties.put("Transaction-Type", "request");
        properties.put("Method", String.valueOf(request.getMethod()));
        properties.put("Url", String.valueOf(request.getRequestURI()));
        properties.put("Content-Type", request.getContentType());
        properties.put(HttpHeaders.HOST, this.request.getHeader(HttpHeaders.HOST));
        properties.put(HttpHeaders.ACCEPT, request.getHeader(HttpHeaders.ACCEPT));
        return properties;
    }
}
