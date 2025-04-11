package no.nav.dolly.libs.security.config;

import lombok.experimental.UtilityClass;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;

/**
 * Utility class for configuring Spring Security for a reactive web application.
 */
@UtilityClass
public class DollyServerHttpSecurity {

    /**
     * Allow access to certain common endpoints ({@code /error}, {@code /internal}, {@code /swagger} etc.) without authentication,
     * but avoid completing the configuration with {@code .anyExchange().authenticated()}, allowing for further configuration.
     * @return A customizer for use with {@code .authorizeExchange(...)}.
     */
    public static Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> allowDefaultHttpRequests() {
        return spec -> spec
                .pathMatchers(
                        "/error",
                        "/internal/**",
                        "/swagger",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**")
                .permitAll();
    }

    /**
     * Allow access to certain common endpoints ({@code /error}, {@code /internal}, {@code /swagger} etc.) without authentication,
     * protecting all others.
     * @return A customizer for use with {@code .authorizeExchange(...)}.
     */
    public static Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> withDefaultHttpRequests() {
        return spec -> {
            allowDefaultHttpRequests().customize(spec);
            spec.anyExchange().authenticated();
        };
    }

}
