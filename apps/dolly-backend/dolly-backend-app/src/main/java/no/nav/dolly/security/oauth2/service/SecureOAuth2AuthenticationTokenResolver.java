package no.nav.dolly.security.oauth2.service;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Optional;

public record SecureOAuth2AuthenticationTokenResolver(
        OAuth2AuthorizedClientService clientService) implements AuthenticationTokenResolver {

    @Override
    public String getTokenValue() {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = oauth2AuthenticationToken();
        String clientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                clientRegistrationId,
                oAuth2AuthenticationToken.getName()
        );
        return client.getAccessToken().getTokenValue();
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
        if (!oauth2AuthenticationToken().isAuthenticated()) {
            throw new CredentialsExpiredException("Token er utloept");
        }
    }

    private OAuth2AuthenticationToken oauth2AuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(o -> o instanceof OAuth2AuthenticationToken)
                .map(OAuth2AuthenticationToken.class::cast)
                .orElseThrow(() -> new RuntimeException("Finner ikke Authentication Token"));
    }
}
