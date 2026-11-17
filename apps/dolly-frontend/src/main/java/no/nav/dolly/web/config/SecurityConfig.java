package no.nav.dolly.web.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.csrf.CsrfProtection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@Profile({ "prod", "dev", "test" })
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return CsrfProtection.apply(http)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(spec -> spec
                        .pathMatchers("/session/**").authenticated()
                        .anyExchange()
                        .permitAll())
                .oauth2ResourceServer(spec -> spec.jwt(Customizer.withDefaults()))
                .build();
    }
}
