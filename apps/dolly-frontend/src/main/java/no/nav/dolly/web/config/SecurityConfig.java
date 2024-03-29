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
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Slf4j
@Configuration
@Profile({ "prod", "dev" })
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Value("${spring.security.oauth2.resourceserver.aad.issuer-uri}")
    private String aadIssuer;

    @SneakyThrows
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {

        http.cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll())
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(jwtSpec -> jwtDecoder()));
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(aadIssuer);
    }
}
