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
     * <p>Allow access to certain common endpoints ({@code /error}, {@code /internal}, {@code /swagger} etc.) without authentication, protecting all others.</p>
     * <p>Customize further as needed after calling this method, if necessary</p>
     * @return A customizer for use as {@code .authorizeExchange(DollyServerHttpSecurity.withDefaultHttpRequests())}.
     */
    public static Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> withDefaultHttpRequests() {
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
                .permitAll()
                .anyExchange()
                .authenticated();

    }

}
