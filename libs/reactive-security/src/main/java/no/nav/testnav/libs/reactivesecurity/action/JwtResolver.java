package no.nav.testnav.libs.reactivesecurity.action;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZonedDateTime;

@Slf4j
@SuppressWarnings("java:S1610")
abstract class JwtResolver {

    Mono<JwtAuthenticationToken> getJwtAuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .switchIfEmpty(Mono.error(new JwtResolverException("ReactiveSecurityContext is empty")))
                .doOnNext(context -> log.info("JwtResolver context.authentication {} {}", context.getAuthentication().getClass().getCanonicalName(), context.getAuthentication()))
                .map(SecurityContext::getAuthentication)
                .map(JwtAuthenticationToken.class::cast)
                .doOnError(throwable -> log.warn("Klarte ikke hente Jwt Auth Token", throwable))
                .doOnSuccess(jwtAuthenticationToken -> {
                    Jwt credentials = (Jwt) jwtAuthenticationToken.getCredentials();
                    Instant expiresAt = credentials.getExpiresAt();
                    if (expiresAt == null || expiresAt.isBefore(ZonedDateTime.now().toInstant().plusSeconds(120))) {
                        throw new CredentialsExpiredException("Jwt er utløpt eller utløper innen kort tid");
                    }
                });
    }

}
