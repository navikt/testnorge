package no.nav.testnav.apps.profilapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;

import no.nav.testnav.libs.reactivesecurity.config.OAuth2ResourceServerConfiguration;


@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration extends OAuth2ResourceServerConfiguration {

    public SecurityConfiguration(
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
            @Value("${spring.security.oauth2.resourceserver.jwt.accepted-audience}") List<String> acceptedAudience,
            @Value("${http.proxy:#{null}}") String proxyHost
    ) {
        super(oAuth2ResourceServerProperties, acceptedAudience, proxyHost);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
                .authorizeExchange()
                .pathMatchers(
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/internal/isReady",
                        "/internal/isAlive"
                ).permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtDecoder(jwtDecoder());
        return http.build();
    }

}

