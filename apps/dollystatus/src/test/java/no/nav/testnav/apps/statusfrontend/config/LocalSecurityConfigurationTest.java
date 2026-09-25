package no.nav.testnav.apps.statusfrontend.config;

import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

class LocalSecurityConfigurationTest {

    private static final String REGISTRATION_ID = "aad";
    private static final String USER_ID = "local-user";

    @Test
    void shouldTreatLocalAadLoginAsAzureAd() {
        var authentication = authentication();
        var resourceServerType = new LocalAuthenticatedResourceServerType();

        StepVerifier.create(resourceServerType.call()
                        .contextWrite(withAuthentication(authentication)))
                .expectNext(ResourceServerType.AZURE_AD)
                .verifyComplete();
    }

    @Test
    void shouldUseLocalPrincipalNameAsTokenCacheKey() {
        var authentication = authentication();
        var resourceServerType = new LocalAuthenticatedResourceServerType();
        var authenticatedUserId = new LocalAuthenticatedUserId(resourceServerType);

        StepVerifier.create(authenticatedUserId.call()
                        .contextWrite(withAuthentication(authentication)))
                .expectNext(USER_ID)
                .verifyComplete();
    }

    @Test
    void shouldReuseLocalAuthorizedClientAccessToken() {
        var authentication = authentication();
        var authorizedClientService = mock(ReactiveOAuth2AuthorizedClientService.class);
        var accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "local-access-token",
                Instant.now(),
                Instant.now().plusSeconds(3600));
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                clientRegistration(),
                USER_ID,
                accessToken);
        when(authorizedClientService.<OAuth2AuthorizedClient>loadAuthorizedClient(REGISTRATION_ID, USER_ID))
                .thenReturn(Mono.just(authorizedClient));
        var authenticatedToken = new LocalAuthenticatedToken(
                new LocalAuthenticatedResourceServerType(),
                authorizedClientService);

        StepVerifier.create(authenticatedToken.call()
                        .contextWrite(withAuthentication(authentication)))
                .assertNext(token -> {
                    assertThat(token.getAccessTokenValue())
                            .isEqualTo("local-access-token");
                    assertThat(token.getUserId())
                            .isEqualTo(USER_ID);
                })
                .verifyComplete();
    }

    private OAuth2AuthenticationToken authentication() {
        var authority = new SimpleGrantedAuthority("ROLE_USER");
        var principal = new DefaultOAuth2User(
                List.of(authority),
                Map.of("sub", USER_ID),
                "sub");
        return new OAuth2AuthenticationToken(principal, List.of(authority), REGISTRATION_ID);
    }

    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .clientId("local-client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:3000/login/oauth2/code/aad")
                .authorizationUri("https://example.invalid/oauth2/authorize")
                .tokenUri("https://example.invalid/oauth2/token")
                .build();
    }
}
