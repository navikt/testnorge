package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Slf4j
@Configuration
@Profile("idporten")
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class IdportenSecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.tokenx.jwk-set-uri}")
    private String jwkSetUri; //TODO SLETT MEG: Endre til denne

    @SneakyThrows
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {

        http.cors()
                .and().csrf().disable()
                .authorizeExchange()
                .anyExchange()
                .permitAll()
                .and().oauth2ResourceServer().jwt(jwt -> jwtDecoder());
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        return NimbusReactiveJwtDecoder.withJwkSetUri("testytest").build();
    }
}
