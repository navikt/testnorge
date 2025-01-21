package no.nav.testnav.libs.securitycore.oauth2.client.userinfo;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AutoConfiguration
public class NoopReactiveOauth2AutoConfiguration {

    @Bean
    ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> reactiveOAuth2UserService() {
        return new NoopReactiveOAuth2UserService();
    }

    @Bean
    OidcReactiveOAuth2UserService oidcReactiveOAuth2UserService() {
        return new NoopOidcReactiveOAuth2UserService();
    }

    /**
     * When testing, just use an empty repository so we don't trigger the full behaviour of
     * {@code InMemoryReactiveClientRegistrationRepository} - which is not needed for tests.
     * @return An instance of {@code NoopReactiveClientRegistrationRepository}.
     * @see org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
     * @see no.nav.testnav.libs.securitycore.oauth2.client.userinfo.NoopReactiveClientRegistrationRepository
     */
    @Bean
    @Profile("test")
    ReactiveClientRegistrationRepository reactiveClientRegistrationRepositoryForTesting() {
        return new NoopReactiveClientRegistrationRepository();
    }

}
