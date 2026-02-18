package no.nav.registre.sdforvalter.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.security.config.DollyServerHttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@Profile({"prod", "local"})
class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity, JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(DollyServerHttpSecurity.withDefaultHttpRequests())
                .oauth2ResourceServer(c -> c.jwt(jwtSpec -> jwtSpec.authenticationManager(jwtReactiveAuthenticationManager)))
                .build();
    }

}
