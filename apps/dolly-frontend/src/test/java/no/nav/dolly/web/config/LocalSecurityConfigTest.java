package no.nav.dolly.web.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import static no.nav.dolly.web.config.IdportenSecurityConfigTest.assertRedirectsToLoginPage;
import static no.nav.dolly.web.config.IdportenSecurityConfigTest.securityWithClientRegistrations;

class LocalSecurityConfigTest {

    @Test
    void shouldBuildSecurityChainAndRedirectUnauthenticatedNavigationToLoginPage() {
        var registration = ClientRegistration
                .withRegistrationId("aad")
                .clientId("dolly-frontend")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://login.test/authorize")
                .tokenUri("https://login.test/token")
                .build();

        var chain = new LocalSecurityConfig()
                .configure(securityWithClientRegistrations(new InMemoryReactiveClientRegistrationRepository(registration)));

        assertRedirectsToLoginPage(chain);
    }
}
