package no.nav.dolly.proxy.texas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
class SecurityConfig {

    @Value("${dolly.texas.shared.secret}")
    private String sharedSecret;

    private static final String[] WHITELIST = {
            "/internal/**",
            "/swagger",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/swagger-ui/**"
    };

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        var filter = new DollyAuthorizationHeaderFilter(sharedSecret, WHITELIST);
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(WHITELIST).permitAll()
                        .anyExchange().permitAll())
                .build();

    }

}