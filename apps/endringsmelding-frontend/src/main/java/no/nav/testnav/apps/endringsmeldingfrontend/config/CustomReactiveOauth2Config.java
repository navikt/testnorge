package no.nav.testnav.apps.endringsmeldingfrontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
public class CustomReactiveOauth2Config {

    @Bean
    public ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> reactiveOAuth2UserService() {
        return new CustomReactiveOAuth2UserService();
    }

    @Bean
    public OidcReactiveOAuth2UserService oidcReactiveOAuth2UserService() {
        return new CustomOidcReactiveOAuth2UserService();
    }

}
