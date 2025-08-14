package no.nav.testnav.apps.tpsmessagingfrontend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final AzureAdResourceServerProperties config;

    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(spec -> spec
                        .anyExchange()
                        .permitAll())
                .oauth2ResourceServer(spec -> spec.jwt(jwtSpec -> jwtDecoder()))
                .build();
    }

    @Bean("jwtDecoder")
    @Profile("!test")
    ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(config.getIssuerUri());
    }

    @Bean("jwtDecoder")
    @Profile("test")
    ReactiveJwtDecoder jwtDecoderForTest() {
        return token -> Mono.empty();
    }

}
