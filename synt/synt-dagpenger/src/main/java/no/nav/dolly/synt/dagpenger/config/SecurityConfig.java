package no.nav.dolly.synt.dagpenger.config;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.texas.TexasTokenIntrospector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final TexasTokenIntrospector tokenIntrospector;

    @Bean
    @Profile("!prod")
    SecurityWebFilterChain testFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .anyExchange().permitAll())
                .build();
    }

    @Bean
    @Profile("prod")
    SecurityWebFilterChain prodFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(
                                "/internal/**",
                                "/metrics",
                                "/swagger",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api/api-docs/**",
                                "/v3/api-docs/**",
                                "/webjars/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(opaque -> opaque.introspector(tokenIntrospector)))
                .build();
    }

}
