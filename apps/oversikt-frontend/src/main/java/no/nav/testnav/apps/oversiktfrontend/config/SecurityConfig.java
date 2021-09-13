package no.nav.testnav.apps.oversiktfrontend.config;

import com.nimbusds.jose.jwk.JWK;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import no.nav.testnav.libs.reactivesessionsecurity.handler.LogoutSuccessHandler;
import no.nav.testnav.libs.reactivesessionsecurity.manager.AuthorizationCodeReactiveAuthenticationManger;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.logut.IdportenOcidLogoutUrlResolver;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String jwk;
    private final String wellKnownUrl;
    private final String postLogoutRedirectUri;

    public SecurityConfig(
            @Value("${spring.security.oauth2.client.provider.idporten.issuer-uri}.well-known/openid-configuration") String wellKnownUrl,
            @Value("${spring.security.oauth2.client.registration.idporten.post-logout-redirect-uri}") String postLogoutRedirectUri,
            @Value("${IDPORTEN_CLIENT_JWK}") String jwk
    ) {
        this.jwk = jwk;
        this.wellKnownUrl = wellKnownUrl;
        this.postLogoutRedirectUri = postLogoutRedirectUri;
    }

    @SneakyThrows
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        var authenticationManger = new AuthorizationCodeReactiveAuthenticationManger(JWK.parse(jwk));
        var logoutSuccessHandler = new LogoutSuccessHandler();
        logoutSuccessHandler.applyOn("idporten", new IdportenOcidLogoutUrlResolver(wellKnownUrl, postLogoutRedirectUri));

        return http.cors()
                .and().csrf().disable()
                .authorizeExchange()
                .pathMatchers(
                        "/internal/isReady",
                        "/internal/isAlive",
                        "/favicon.ico",
                        "/login",
                        "/main.*.css",
                        "/manifest.json",
                        "/bundle.*.js"
                ).permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec.authenticationManager(authenticationManger))
                .logout(logoutSpec -> logoutSpec
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler))
                .build();
    }
}