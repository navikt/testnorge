package no.nav.testnav.libs.securitycore.oauth2.client.userinfo;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AutoConfiguration
public class NoopReactiveOauth2AutoConfiguration {

    @Bean
    public ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> reactiveOAuth2UserService() {
        return new NoopReactiveOAuth2UserService();
    }

    @Bean
    public OidcReactiveOAuth2UserService oidcReactiveOAuth2UserService() {
        return new NoopOidcReactiveOAuth2UserService();
    }

}
