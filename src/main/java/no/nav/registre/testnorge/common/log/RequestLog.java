package no.nav.registre.testnorge.common.log;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import no.nav.registre.testnorge.common.headers.NavHeaders;

@Slf4j
public class RequestLog {


    private final ContentCachingRequestWrapper request;

    public RequestLog(ContentCachingRequestWrapper request) {
        this.request = request;
    }

    private String getBody() throws IOException {
        return new String(request.getContentAsByteArray(), request.getCharacterEncoding());
    }

    public void log() {
        try {
            MDC.clear();
            MDC.setContextMap(this.toPropertyMap());
            log.trace(getBody());
            MDC.clear();
        } catch (Exception e) {
            log.error("Klarer ikke aa lese fra requsten", e);
        }
    }

    private Map<String, String> toPropertyMap() {
        Map<String, String> properties = new HashMap<>();
        properties.put("Type", "REQUEST");
        properties.put("Method", String.valueOf(request.getMethod()));
        properties.put("Url", String.valueOf(request.getRequestURI()));
        properties.put("Content-Type", request.getContentType());
        properties.put(NavHeaders.UUID, request.getHeader(NavHeaders.UUID));
        properties.put(HttpHeaders.HOST, this.request.getHeader(HttpHeaders.HOST));
        properties.put(HttpHeaders.ACCEPT, request.getHeader(HttpHeaders.ACCEPT));
        return properties;
    }

}
