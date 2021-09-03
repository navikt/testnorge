package no.nav.dolly.web.config;

import com.nimbusds.jose.jwk.JWK;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import no.nav.testnav.libs.reactivesecurity.service.AuthorizationCodeReactiveAuthenticationManger;

@Slf4j
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${IDPORTEN_CLIENT_JWK}")
    String jwk;

    @SneakyThrows
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        var authenticationManger = new AuthorizationCodeReactiveAuthenticationManger(JWK.parse(jwk));
        return http.cors()
                .and().csrf().disable()
                .authorizeExchange()
                .pathMatchers(
                        "/internal/isReady",
                        "/internal/isAlive",
                        "/favicon.ico",
                        "/login",
                        "/main.*.css",
                        "/bundle.*.js"
                ).permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec.authenticationManager(authenticationManger))
                .formLogin().loginPage("/login")
                .and().build();
    }
}