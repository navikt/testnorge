package no.nav.dolly.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.SneakyThrows;

@EnableWebFluxSecurity
public class SecurityConfig {

    @SneakyThrows
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
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
                .and()
                .formLogin().loginPage("/login")
                .and().build();
    }
}