package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.config.authentication.DollyAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;


@Slf4j
@Configuration
@Profile({ "prod", "dev" })
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final Environment environment;
    @Value("${spring.security.oauth2.resourceserver.aad.issuer-uri}")
    private String aadIssuer;

    @Value("${spring.security.oauth2.resourceserver.idporten.issuer-uri}")
    private String idportenIssuer;

    @SneakyThrows
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {

        http.cors()
                .and().csrf().disable()
                .authorizeExchange()
                .anyExchange()
                .permitAll()
                .and().oauth2ResourceServer().jwt(jwt -> jwtDecoder()).and()
                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec
                        .authenticationSuccessHandler(new DollyAuthenticationSuccessHandler()))
                .formLogin().loginPage("/login");
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(Arrays.asList(environment.getActiveProfiles()).contains("idporten") ? idportenIssuer : aadIssuer);
    }
}
