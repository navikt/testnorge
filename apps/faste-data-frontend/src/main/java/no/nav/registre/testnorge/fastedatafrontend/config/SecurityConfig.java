package no.nav.registre.testnorge.fastedatafrontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http.csrf().disable()
                .requestCache().requestCache(NoOpServerRequestCache.getInstance()).and()
                .authorizeExchange()
                .pathMatchers("/internal/isReady", "/internal/isAlive").permitAll()
                .anyExchange().authenticated().and()
                .oauth2Login();
        return http.build();
    }
}