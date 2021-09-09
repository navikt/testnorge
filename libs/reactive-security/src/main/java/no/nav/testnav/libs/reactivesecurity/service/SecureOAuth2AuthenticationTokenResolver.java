package no.nav.testnav.libs.reactivesecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.reactivesecurity.domain.Token;

@Slf4j
@RequiredArgsConstructor
public class SecureOAuth2AuthenticationTokenResolver implements AuthenticationTokenResolver {

    private final ReactiveOAuth2AuthorizedClientService auth2AuthorizedClientService;

    private Mono<OAuth2AuthenticationToken> oauth2AuthenticationToken() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(OAuth2AuthenticationToken.class::cast)
                .doOnSuccess(oAuth2AuthenticationToken -> {
                    if (!oAuth2AuthenticationToken.isAuthenticated()) {
                        throw new CredentialsExpiredException("Token er utloept");
                    }
                });
    }

    @Override
    public Mono<Token> getToken() {
        return oauth2AuthenticationToken()
                .flatMap(oAuth2AuthenticationToken -> auth2AuthorizedClientService.loadAuthorizedClient(
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                                oAuth2AuthenticationToken.getPrincipal().getName()
                        ).doOnError(error -> log.error("Feil med load", error)).doOnSuccess(value -> log.info("value: {}")).map(client -> client.getAccessToken().getTokenValue())
                ).map(token -> Token.builder()
                        .value(token)
                        .clientCredentials(false)
                        .oid(null)
                        .build()
                ).doOnError(error -> log.error("Feil", error))
                .doOnSuccess(token -> log.info("Token {}", token))
                .doFinally(value -> log.info("Ferdig? {}", value));
    }

    @Override
    public Mono<String> getClientRegistrationId() {
        return oauth2AuthenticationToken().map(OAuth2AuthenticationToken::getAuthorizedClientRegistrationId);
    }
}
