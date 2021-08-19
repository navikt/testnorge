package no.nav.testnav.libs.reactivecore.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;

public interface LogRequest {
    void log(ServerHttpRequest request, String body);
}
