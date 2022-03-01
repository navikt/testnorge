package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import reactor.core.publisher.Mono;

@Slf4j
abstract class Oauth2AuthenticationToken {
    Mono<OAuth2AuthenticationToken> oauth2AuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(OAuth2AuthenticationToken.class::cast)
                .doOnError(throwable -> {
                    log.error("Feilet med å hente accessToken", throwable);
                })
                .doOnSuccess(oAuth2AuthenticationToken -> {
                    if (!oAuth2AuthenticationToken.isAuthenticated()) {
                        throw new CredentialsExpiredException("Token er utloept");
                    }
                });
    }
}
