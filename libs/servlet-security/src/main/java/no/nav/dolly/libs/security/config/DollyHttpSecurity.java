package no.nav.dolly.libs.security.config;

import lombok.experimental.UtilityClass;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility class for configuring Spring Security for a servlet web application.
 */
@UtilityClass
public class DollyHttpSecurity {

    private static final String[] OPEN_ENDPOINTS = new String[]{
            "/error",
            "/internal/**",
            "/swagger",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**"
    };

    /**
     * Allow access to certain common endpoints ({@code /error}, {@code /internal}, {@code /swagger} etc.) without authentication,
     * protecting all others.
     *
     * @param including Additional endpoints to include in the open list.
     * @return A customizer for use with {@code .authorizeHttpRequests(...)}.
     */
    public static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> withDefaultHttpRequests(String... including) {
        return registry -> {
            var endpoints = Optional
                    .ofNullable(including)
                    .map(paths -> Stream
                            .concat(Arrays.stream(OPEN_ENDPOINTS), Arrays.stream(paths))
                            .toArray(String[]::new))
                    .orElse(OPEN_ENDPOINTS);
            registry
                    .requestMatchers(endpoints).permitAll()
                    .anyRequest().fullyAuthenticated();
        };
    }

}
