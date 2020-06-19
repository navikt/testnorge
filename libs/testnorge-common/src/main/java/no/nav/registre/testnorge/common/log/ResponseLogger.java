package no.nav.registre.testnorge.common.log;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import no.nav.registre.testnorge.common.headers.NavHeaders;

@Slf4j
public class ResponseLogger {

    private final ContentCachingResponseWrapper response;

    public ResponseLogger(ContentCachingResponseWrapper response) {
        this.response = response;
    }

    private String getBody() throws IOException {
        String body = new String(response.getContentAsByteArray(), response.getCharacterEncoding());
        response.copyBodyToResponse();
        return body;
    }

    public void log(String uuid) {
        try {
            MDC.setContextMap(this.toPropertyMap(uuid));
            var body = getBody();
            log.trace(body.equals("") ? "[empty]" : body);
        } catch (IOException e) {
            log.error("Klarer ikke aa lese fra response", e);
        }
    }

    private Map<String, String> toPropertyMap(String uuid) {
        Map<String, String> properties = new HashMap<>();
        properties.put("Transaction-Type", "response");
        properties.put("Status-Code", String.valueOf(response.getStatusCode()));
        if (response.getContentType() != null) {
            properties.put("Content-Type", response.getContentType());
        }
        properties.put(NavHeaders.UUID, uuid);
        return properties;
    }
}
