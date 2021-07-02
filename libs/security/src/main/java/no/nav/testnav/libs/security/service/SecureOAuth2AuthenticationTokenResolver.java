package no.nav.testnav.libs.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SecureOAuth2AuthenticationTokenResolver implements AuthenticationTokenResolver {

    private final ReactiveOAuth2AuthorizedClientService auth2AuthorizedClientService;

    private Mono<OAuth2AuthenticationToken> oauth2AuthenticationToken() {

        return ReactiveSecurityContextHolder
                .getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .map(OAuth2AuthenticationToken.class::cast);
    }

    @Override
    public Mono<String> getTokenValue() {
        return oauth2AuthenticationToken()
                .flatMap(oAuth2AuthenticationToken -> auth2AuthorizedClientService.loadAuthorizedClient(
                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                        oAuth2AuthenticationToken.getPrincipal().getName()
                ).map(client -> client.getAccessToken().getTokenValue()));
    }

    @Override
    public boolean isClientCredentials() {
        return false;
    }

    @Override
    public String getOid() {
        return null;
    }

    @Override
    public void verifyAuthentication() {
        oauth2AuthenticationToken()
                .doOnSuccess(oAuth2AuthenticationToken -> {
                    if (!oAuth2AuthenticationToken.isAuthenticated()) {
                        throw new CredentialsExpiredException("Token er utloept");
                    }
                });
    }
}
