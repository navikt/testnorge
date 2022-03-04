package no.nav.registre.testnav.ameldingservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable()
                .authorizeExchange()
                .pathMatchers(
                        "/swagger",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/internal/isReady",
                        "/internal/isAlive"
                ).permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt(spec -> spec.authenticationManager(jwtReactiveAuthenticationManager))
                .and().build();
    }

}


