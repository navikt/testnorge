package no.nav.dolly.budpro;

import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Import({
        SecureOAuth2ServerToServerConfiguration.class,
        InsecureJwtServerToServerConfiguration.class
})
@Profile("!test")
public class SecurityConfig {

    @Value("${app.security.allow-api:false}")
    private boolean allowApi;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                            authorize
                                    .requestMatchers(
                                            "/internal/**",
                                            "/webjars/**",
                                            "/swagger-resources/**",
                                            "/v3/api-docs/**",
                                            "/swagger-ui/**",
                                            "/swagger",
                                            "/error",
                                            "/swagger-ui.html")
                                    .permitAll();
                            if (allowApi) {
                                authorize
                                        .requestMatchers("/api/**")
                                        .permitAll();
                            } else {
                                authorize
                                        .requestMatchers("/api/**")
                                        .fullyAuthenticated();
                            }
                        }
                )
                .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()))
                .build();
    }

}
