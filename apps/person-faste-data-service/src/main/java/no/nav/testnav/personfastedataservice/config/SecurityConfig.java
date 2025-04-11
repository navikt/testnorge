package no.nav.testnav.personfastedataservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.security.config.DollyServerHttpSecurity;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(DollyServerHttpSecurity.withDefaultHttpRequests())
                .oauth2ResourceServer(oauth2RSConfig -> oauth2RSConfig.jwt(jwtSpec -> jwtSpec.authenticationManager(jwtReactiveAuthenticationManager)))
                .build();
    }

}
