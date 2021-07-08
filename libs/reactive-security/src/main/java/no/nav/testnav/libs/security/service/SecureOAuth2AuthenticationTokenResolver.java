package no.nav.testnav.libs.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.security.domain.Token;

@RequiredArgsConstructor
public class SecureOAuth2AuthenticationTokenResolver implements AuthenticationTokenResolver {

    private final ReactiveOAuth2AuthorizedClientService auth2AuthorizedClientService;

    private Mono<OAuth2AuthenticationToken> oauth2AuthenticationToken() {

        return ReactiveSecurityContextHolder
                .getContext()
                .map(securityContext -> securityContext.getAuthentication())
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
                ).map(client -> client.getAccessToken().getTokenValue()))
                .map(token -> Token.builder()
                        .value(token)
                        .clientCredentials(false)
                        .oid(null)
                        .build()
                );
    }
}
