package no.nav.dolly.libs.security.config;

import lombok.experimental.UtilityClass;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility class for configuring Spring Security for a reactive web application.
 */
@UtilityClass
public class DollyServerHttpSecurity {

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
     * @return A customizer for use with {@code .authorizeExchange(...)}.
     */
    public static Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> withDefaultHttpRequests(String... including) {
        return spec -> {
            var endpoints = Optional
                    .ofNullable(including)
                    .map(paths -> Stream
                            .concat(Arrays.stream(OPEN_ENDPOINTS), Arrays.stream(paths))
                            .toArray(String[]::new))
                    .orElse(OPEN_ENDPOINTS);
            spec
                    .pathMatchers(endpoints).permitAll()
                    .anyExchange().authenticated();
        };
    }

}
