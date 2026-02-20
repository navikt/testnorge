package no.nav.testnav.apps.organisasjonbestillingservice.config;

import no.nav.dolly.libs.security.config.DollyServerHttpSecurity;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
class SecurityConfig {

    @Bean
    @SuppressWarnings("java:S4502")
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(DollyServerHttpSecurity.withDefaultHttpRequests("/h2/**", "/member/**"))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec -> jwtSpec.authenticationManager(jwtReactiveAuthenticationManager)))
                .build();
    }

}
