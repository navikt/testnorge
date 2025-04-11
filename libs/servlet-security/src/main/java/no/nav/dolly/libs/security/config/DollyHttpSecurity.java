package no.nav.dolly.libs.security.config;

import lombok.experimental.UtilityClass;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Utility class for configuring Spring Security for a servlet web application.
 */
@UtilityClass
public class DollyHttpSecurity {

    /**
     * <p>Allow access to certain common endpoints ({@code /error}, {@code /internal}, {@code /swagger} etc.) without authentication, protecting all others.</p>
     * <p>Customize further as needed after calling this method, if necessary.</p>
     * @return A customizer for use as {@code .authorizeHttpRequests(DollyHttpSecurity.withDefaultHttpRequests())}.
     */
    public static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> withDefaultHttpRequests() {
        return registry -> registry
                .requestMatchers(
                        "/error",
                        "/internal/**",
                        "/swagger",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**")
                .permitAll()
                .requestMatchers(
                        "/api/**")
                .fullyAuthenticated();
    }

}
