package no.nav.dolly.web.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.handler.LogoutSuccessHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Slf4j
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {

        var logoutSuccessHandler = new LogoutSuccessHandler();

        return http.cors()
                .and().csrf().disable()
                .authorizeExchange()
                .pathMatchers(
                        "/internal/isReady",
                        "/internal/isAlive",
                        "/oauth2/callback",
                        "/favicon.ico",
                        "/login",
                        "/main.*.css",
                        "/bundle.*.js",
                        "/*.png"
                ).permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login()
                .and().formLogin().loginPage("/login")
                .and().logout(logoutSpec -> logoutSpec
                        .logoutUrl("/logout")
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout"))
                        .logoutSuccessHandler(logoutSuccessHandler)
                ).build();
    }

    //    TODO Use when idporten is ready
    //    private final String jwk;
    //    private final String wellKnownUrl;
    //    private final String postLogoutRedirectUri;
    //
    //    public SecurityConfig(
    //            @Value("${spring.security.oauth2.client.provider.idporten.issuer-uri}.well-known/openid-configuration") String wellKnownUrl,
    //            @Value("${spring.security.oauth2.client.registration.idporten.post-logout-redirect-uri}") String postLogoutRedirectUri,
    //            @Value("${IDPORTEN_CLIENT_JWK}") String jwk
    //    ) {
    //        this.jwk = jwk;
    //        this.wellKnownUrl = wellKnownUrl;
    //        this.postLogoutRedirectUri = postLogoutRedirectUri;
    //    }
    //    @SneakyThrows
    //    @Bean
    //    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
    //        var authenticationManger = new AuthorizationCodeReactiveAuthenticationManger(JWK.parse(jwk));
    //        var logoutSuccessHandler = new LogoutSuccessHandler();
    //        logoutSuccessHandler.applyOn("idporten", new IdportenOcidLogoutUrlResolver(wellKnownUrl, postLogoutRedirectUri));
    //
    //        return http.cors()
    //                .and().csrf().disable()
    //                .authorizeExchange()
    //                .pathMatchers(
    //                        "/internal/isReady",
    //                        "/internal/isAlive",
    //                        "/favicon.ico",
    //                        "/login",
    //                        "/main.*.css",
    //                        "/bundle.*.js"
    //                ).permitAll()
    //                .anyExchange().authenticated()
    //                .and().oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec.authenticationManager(authenticationManger))
    //                .formLogin().loginPage("/login")
    //                .and().logout(logoutSpec -> logoutSpec
    //                        .logoutUrl("/logout")
    //                        .logoutSuccessHandler(logoutSuccessHandler))
    //                .build();
    //    }
}