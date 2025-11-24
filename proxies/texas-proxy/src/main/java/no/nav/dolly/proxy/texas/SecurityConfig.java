package no.nav.dolly.proxy.texas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

@EnableWebFluxSecurity
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

    /**
     * Security chain for /token/** endpoints, secured by Azure AD JWT validation.
     * This chain is ordered first to ensure it handles /token/** requests before the default chain.
     */
    @Bean
    @Order(1)
    @Profile("!test")
    SecurityWebFilterChain tokenApiSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/token/**"))
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    /**
     * Default security chain for all other endpoints, secured by a shared secret.
     * This chain has a lower order and acts as a fallback.
     */
    @Bean
    @Order(2)
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        var filter = new DollyAuthorizationHeaderFilter(sharedSecret, WHITELIST);
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .build();
    }
}