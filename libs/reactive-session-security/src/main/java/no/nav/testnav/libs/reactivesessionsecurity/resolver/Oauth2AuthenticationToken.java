package no.nav.testnav.libs.reactivesessionsecurity.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class Oauth2AuthenticationToken {
    Mono<OAuth2AuthenticationToken> oauth2AuthenticationToken(Mono<Authentication> authentication) {
        return authentication
                .map(auth -> {
                    if (auth instanceof OAuth2AuthenticationToken authenticationToken) {
                        return authenticationToken;
                    }
                    throw new OAuth2AuthenticationException("Auth er ikke av type Oauth2AuthenticationToken");
                })
                .doOnError(throwable -> log.error("Feilet med å hente accessToken", throwable))
                .doOnSuccess(oAuth2AuthenticationToken -> {
                    if (!oAuth2AuthenticationToken.isAuthenticated()) {
                        throw new CredentialsExpiredException("Token er utløpt");
                    }
                });
    }
}