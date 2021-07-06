package no.nav.testnav.libs.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

import no.nav.testnav.libs.security.domain.Token;

@RequiredArgsConstructor
public class SecureJwtAuthenticationTokenResolver implements AuthenticationTokenResolver {

    private Mono<JwtAuthenticationToken> getJwtAuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .map(JwtAuthenticationToken.class::cast)
                .doOnSuccess(jwtAuthenticationToken -> {
                    Jwt credentials = (Jwt) jwtAuthenticationToken.getCredentials();
                    Instant expiresAt = credentials.getExpiresAt();
                    if (expiresAt == null || expiresAt.isBefore(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC))) {
                        throw new CredentialsExpiredException("Jwt er utloept");
                    }
                });
    }

    @Override
    public Mono<Token> getToken() {
        return getJwtAuthenticationToken()
                .map(jwtAuthenticationToken -> {
                    var tokenAttributes = jwtAuthenticationToken.getTokenAttributes();
                    return Token.builder()
                            .value(jwtAuthenticationToken.getToken().getTokenValue())
                            .clientCredentials(String.valueOf(tokenAttributes.get("oid"))
                                    .equals(String.valueOf(tokenAttributes.get("sub"))))
                            .oid(String.valueOf(tokenAttributes.get("oid")))
                            .build();
                });
    }
}
