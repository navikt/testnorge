package no.nav.dolly.libs.security.config;

import lombok.experimental.UtilityClass;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@UtilityClass
public class DollyHttpSecurity {

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
