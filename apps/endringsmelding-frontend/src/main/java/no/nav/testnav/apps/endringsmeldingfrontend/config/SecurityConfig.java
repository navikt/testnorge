package no.nav.testnav.apps.endringsmeldingfrontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.cors().and().csrf()
                .disable()
                .authorizeExchange()
                .pathMatchers("/internal/isReady", "/internal/isAlive", "/internal/metrics").permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login()
                .and().build();
    }
}
