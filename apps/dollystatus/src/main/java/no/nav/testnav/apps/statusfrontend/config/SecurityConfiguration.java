package no.nav.testnav.apps.statusfrontend.config;

import no.nav.dolly.libs.security.config.DollyServerHttpSecurity;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;

@Configuration
@Profile("!local")
@EnableWebFluxSecurity
class SecurityConfiguration {

    static final String STATUSOVERSIKT_PATH = "/api/v1/statusoversikt";

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity httpSecurity,
            List<ResourceServerProperties> resourceServerProperties
    ) {
        var tilgangsregler = new ApiTilgangsregler(resourceServerProperties);
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchange -> authorizeExchange
                        .pathMatchers(HttpMethod.GET, STATUSOVERSIKT_PATH)
                        .access(tilgangsregler::azureBrukerEllerTokenX)
                        .pathMatchers("/api/**")
                        .access(tilgangsregler::azureBruker))
                .authorizeExchange(DollyServerHttpSecurity.withDefaultHttpRequests(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/assets/**"
                ))
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()))
                .build();
    }
}
