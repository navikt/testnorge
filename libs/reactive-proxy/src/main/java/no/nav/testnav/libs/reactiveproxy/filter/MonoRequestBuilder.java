package no.nav.testnav.libs.reactiveproxy.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface MonoRequestBuilder {
    Mono<ServerHttpRequest.Builder> build(ServerHttpRequest.Builder original);
}
