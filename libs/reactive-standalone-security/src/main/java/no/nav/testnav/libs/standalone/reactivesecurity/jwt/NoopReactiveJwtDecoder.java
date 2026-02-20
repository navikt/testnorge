package no.nav.testnav.libs.standalone.reactivesecurity.jwt;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

class NoopReactiveJwtDecoder implements ReactiveJwtDecoder {

    @Override
    public Mono<Jwt> decode(String token) {
        return Mono.just(Jwt.withTokenValue("noop").header("alg", "none").claim("sub", "test").build());
    }

}
