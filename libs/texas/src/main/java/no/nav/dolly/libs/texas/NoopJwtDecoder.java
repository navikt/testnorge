package no.nav.dolly.libs.texas; // Or your appropriate test package

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class NoopJwtDecoder implements ReactiveJwtDecoder {

    @Override
    public Mono<Jwt> decode(String token)
            throws JwtException {

        var headers = new HashMap<String, Object>();
        headers.put("alg", "none");
        headers.put("typ", "JWT");

        var claims = new HashMap<String, Object>();
        claims.put("sub", "test-subject");
        claims.put("iss", "noop-issuer");
        claims.put("aud", Collections.singletonList("test-audience"));
        claims.put("jti", UUID.randomUUID().toString());

        var issuedAt = Instant.now();
        var expiresAt = Instant.now().plusSeconds(3600); // Expires in 1 hour

        return Mono.just(new Jwt(token, issuedAt, expiresAt, headers, claims));

    }
}