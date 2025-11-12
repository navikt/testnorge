package no.nav.testnav.pdllagreservice.config;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.security.config.DollyServerHttpSecurity;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Profile("!test")
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(DollyServerHttpSecurity.withDefaultHttpRequests())
                .oauth2ResourceServer(oauth2RSConfig -> oauth2RSConfig.jwt(jwtSpec -> jwtSpec.authenticationManager(jwtReactiveAuthenticationManager))).build();
    }
}