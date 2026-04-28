package no.nav.dolly.libs.texas;

import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
class TexasJwtDecoder implements ReactiveJwtDecoder {

    private final Texas texas;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Jwt> decode(String token)
            throws JwtException {

        return texas
                .introspect(token)
                .flatMap(introspection -> {
                    try {
                        log.info("Token introspection result: {}", introspection);

                        var isActive = objectMapper
                                .readTree(introspection)
                                .path("active").asBoolean(false);
                        if (!isActive) {
                            log.warn("Token is not active according to introspection endpoint: {}", introspection);
                            return Mono.error(new JwtException("Token is not active according to introspection endpoint"));
                        }

                        com.nimbusds.jwt.JWT parsedJwt = com.nimbusds.jwt.JWTParser.parse(token);
                        var claimsSet = parsedJwt.getJWTClaimsSet();
                        if (claimsSet == null) {
                            log.error("Parsed JWT has no claims set: {}", token);
                            return Mono.error(new JwtException("Parsed JWT has no claims"));
                        }
                        var claims = claimsSet.getClaims();

                        var headers = new HashMap<String, Object>();
                        switch (parsedJwt) {
                            case SignedJWT signedJWT when signedJWT.getHeader() != null ->
                                    headers.putAll(signedJWT.getHeader().toJSONObject());
                            case PlainJWT plainJWT when plainJWT.getHeader() != null ->
                                    headers.putAll(plainJWT.getHeader().toJSONObject());
                            default ->
                                    log.warn("JWT is not an instance of SignedJWT or PlainJWT; headers might be incomplete: {}", parsedJwt.getClass().getName());
                        }

                        var issuedAt = claimsSet.getIssueTime() != null ? claimsSet.getIssueTime().toInstant() : null;
                        var expiresAt = claimsSet.getExpirationTime() != null ? claimsSet.getExpirationTime().toInstant() : null;

                        return Mono.just(new Jwt(token, issuedAt, expiresAt, headers, claims));

                    } catch (tools.jackson.core.JacksonException e) {
                        log.error("Failed to parse token introspection response: {}", introspection, e);
                        return Mono.error(new JwtException("Failed to parse token introspection response: " + e.getOriginalMessage(), e));
                    } catch (ParseException e) {
                        log.error("Failed to parse JWT string: {}", token, e);
                        return Mono.error(new JwtException("Failed to parse JWT string: " + e.getMessage(), e));
                    }
                })
                .onErrorMap(throwable -> {
                    if (throwable instanceof JwtException) {
                        return throwable;
                    }
                    if (throwable instanceof TexasException) {
                        log.error("Token introspection failed via Texas API", throwable);
                    } else {
                        log.error("An unexpected error occurred during token decoding or introspection", throwable);
                    }
                    return new JwtException("Token decoding failed: " + throwable.getMessage(), throwable);
                });

    }

}