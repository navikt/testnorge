package no.nav.dolly.budpro;

import no.nav.dolly.libs.security.config.DollyHttpSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;

@Configuration
@EnableWebSecurity
@Profile("!test")
class SecurityConfig {

    @Value("${app.security.allow-api:false}")
    private boolean allowApi;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var additionalOpenEndpoints = HashSet.<String>newHashSet(2);
        additionalOpenEndpoints.add("/failure/**");
        if (allowApi) {
            additionalOpenEndpoints.add("/api/**");
        }
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(DollyHttpSecurity.withDefaultHttpRequests(additionalOpenEndpoints.toArray(new String[]{})))
                .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()))
                .build();
    }

}
