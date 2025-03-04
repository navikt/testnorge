package no.nav.testnav.apps.oversiktfrontend.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final AzureAdResourceServerProperties config;

    @SneakyThrows
    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(spec -> spec
                        .anyExchange()
                        .permitAll())
                .oauth2ResourceServer(spec -> spec.jwt(jwtSpec -> jwtDecoder()))
                .build();
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(config.getIssuerUri());
    }

}
