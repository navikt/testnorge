package no.nav.testnav.libs.reactivecore.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

public interface LogResponse {
    void log(ServerHttpResponse response, ServerHttpRequest request, String body);
}
