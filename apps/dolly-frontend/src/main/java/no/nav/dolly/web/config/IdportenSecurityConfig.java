package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Slf4j
@Configuration
@Profile("idporten")
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class IdportenSecurityConfig {

    private final JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager;

    @SneakyThrows
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {

        http.cors()
                .and().csrf().disable()
                .authorizeExchange()
                .anyExchange()
                .permitAll()
                .and().oauth2ResourceServer()
                .jwt(spec -> spec.authenticationManager(jwtReactiveAuthenticationManager));
        return http.build();
    }
}
