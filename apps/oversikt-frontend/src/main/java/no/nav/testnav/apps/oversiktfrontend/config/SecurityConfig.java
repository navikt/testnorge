package no.nav.testnav.apps.oversiktfrontend.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @SneakyThrows
    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(spec -> spec
                        .anyExchange()
                        .permitAll())
                .oauth2ResourceServer(spec -> spec.jwt(Customizer.withDefaults()))
                .build();
    }
}
