package no.nav.testnav.libs.reactivesecurity.action;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
abstract class JwtResolver {

    Mono<JwtAuthenticationToken> getJwtAuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(JwtAuthenticationToken.class::cast)
                .doOnError(throwable -> log.error("Klarte ikke hente Jwt Auth Token: ", throwable))
                .doOnSuccess(jwtAuthenticationToken -> {
                    Jwt credentials = (Jwt) jwtAuthenticationToken.getCredentials();
                    Instant expiresAt = credentials.getExpiresAt();
                    if (expiresAt == null || expiresAt.isBefore(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC))) {
                        throw new CredentialsExpiredException("Jwt er utloept");
                    }
                });
    }

}
