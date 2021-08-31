package no.nav.dolly.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http.cors()
                .and().csrf().disable()
                .authorizeExchange()
                .pathMatchers("/internal/isReady", "/internal/isAlive", "/oauth2/callback").permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login()
                .and().build();
    }
}