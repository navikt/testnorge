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
class LocalSecurityConfig {

    private static final String LOGOUT = "/logout";
    private static final String LOGIN = "/login";

    @SneakyThrows
    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {
        var authenticationSuccessHandler = new DollyAuthenticationSuccessHandler();
        var logoutSuccessHandler = new LogoutSuccessHandler();

        return http.cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers(
                                "/*.css",
                                "/*.js",
                                "/*.mjs",
                                "/*.png",
                                "/assets/*",
                                "/favicon.ico",
                                "/internal/**",
                                "/oauth2/callback",
                                "/oauth2/logout",
                                LOGIN,
                                LOGOUT
                        ).permitAll()
                        .anyExchange().authenticated())
                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec
                        .authenticationSuccessHandler(authenticationSuccessHandler))
                .formLogin(formLoginSpec -> formLoginSpec.loginPage(LOGIN))
                .logout(logoutSpec -> logoutSpec
                        .logoutUrl(LOGOUT)
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, LOGOUT))
                        .logoutSuccessHandler(logoutSuccessHandler))
                .build();
    }
}