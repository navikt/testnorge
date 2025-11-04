package no.nav.testnav.libs.reactiveproxy.config;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.security.config.DollyServerHttpSecurity;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.reactivesecurity.manager.JwtReactiveAuthenticationManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@Import(SecureOAuth2ServerToServerConfiguration.class)
public class SecurityConfig {

    private final JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager;

    @Bean
    @ConditionalOnMissingBean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(DollyServerHttpSecurity.withDefaultHttpRequests())
                .oauth2ResourceServer(oauth2RSConfig -> oauth2RSConfig.jwt(jwtSpec -> jwtSpec.authenticationManager(jwtReactiveAuthenticationManager)))
                .build();
    }

}
