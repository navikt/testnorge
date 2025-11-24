package no.nav.testnav.libs.reactivesecurity.action;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZonedDateTime;

@Slf4j
@UtilityClass
public class Oauth2Resolver {

    public static Mono<OAuth2AuthenticationToken> getOauth2AuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .switchIfEmpty(Mono.error(new JwtResolverException("ReactiveSecurityContext is empty")))
                .doOnNext(context -> log.info("Oauth2Resolver context.authentication {} {}", context.getAuthentication().getClass().getCanonicalName(), context.getAuthentication()))
                .map(SecurityContext::getAuthentication)
                .map(OAuth2AuthenticationToken.class::cast)
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
