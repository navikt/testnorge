package no.nav.registre.testnorge.common.log;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import no.nav.registre.testnorge.common.headers.NavHeaders;

@Slf4j
public class ResponseLog {

    private final ContentCachingResponseWrapper response;

    public ResponseLog(ContentCachingResponseWrapper response) {
        this.response = response;
    }

    private String getBody() throws IOException {
        return new String(response.getContentAsByteArray(), response.getCharacterEncoding());
    }

    public void log() {
        try {
            MDC.clear();
            MDC.setContextMap(this.toPropertyMap());
            log.trace(getBody());
            MDC.clear();
        } catch (IOException e) {
            log.error("Klarer ikke aa lese body fra response");
        }
    }

    private Map<String, String> toPropertyMap() {
        Map<String, String> properties = new HashMap<>();
        properties.put("Type", "RESPONSE");
        properties.put("Status-Code", String.valueOf(response.getStatusCode()));
        if (response.getContentType() != null) {
            properties.put("Content-Type", response.getContentType());
        }
        HttpHeaders headers = new ServletServerHttpResponse(response).getHeaders();

        properties.put(NavHeaders.UUID, headers.getFirst(NavHeaders.UUID));
        properties.put(HttpHeaders.HOST, headers.getFirst(HttpHeaders.HOST));

        return properties;
    }
}
