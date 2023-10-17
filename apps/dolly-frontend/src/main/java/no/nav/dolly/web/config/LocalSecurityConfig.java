package no.nav.dolly.web.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.authentication.DollyAuthenticationSuccessHandler;
import no.nav.testnav.libs.reactivesessionsecurity.handler.LogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;


@Slf4j
@Configuration
@Profile("local")
@EnableWebFluxSecurity
public class LocalSecurityConfig {

    private static final String LOGOUT = "/logout";
    private static final String LOGIN = "/login";

    @SneakyThrows
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        var authenticationSuccessHandler = new DollyAuthenticationSuccessHandler();
        var logoutSuccessHandler = new LogoutSuccessHandler();

        return http.cors()
                .and().csrf().disable()
                .authorizeExchange()
                .pathMatchers(
                        "/internal/isReady",
                        "/internal/isAlive",
                        "/assets/*",
                        "/internal/metrics",
                        "/oauth2/callback",
                        "/favicon.ico",
                        LOGIN,
                        LOGOUT,
                        "/oauth2/logout",
                        "/*.css",
                        "/*.js",
                        "/*.mjs",
                        "/*.png"
                ).permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec
                        .authenticationSuccessHandler(authenticationSuccessHandler))
                .formLogin().loginPage(LOGIN)
                .and().logout(logoutSpec -> logoutSpec
                        .logoutUrl(LOGOUT)
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, LOGOUT))
                        .logoutSuccessHandler(logoutSuccessHandler))
                .build();
    }
}