package no.nav.testnav.libs.reactivesecurity.jwt;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

class NoopReactiveJwtDecoder implements ReactiveJwtDecoder {

    @Override
    public Mono<Jwt> decode(String token) throws JwtException {
        return Mono.just(Jwt.withTokenValue(null).build());
    }

}
