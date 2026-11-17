package no.nav.testnav.apps.apptilganganalyseservice.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.security.config.DollyServerHttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(DollyServerHttpSecurity.withDefaultHttpRequests())
                .oauth2ResourceServer(oauth2RSConfig -> oauth2RSConfig.jwt(Customizer.withDefaults()))
                .build();
    }

}

