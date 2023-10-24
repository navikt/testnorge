package no.nav.registre.sdforvalter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Remove this call with AzureAd config
 */
@Slf4j
@Configuration
@Order(1)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> c
                        .requestMatchers(
                                antMatcher("/internal/**"),
                                antMatcher("/webjars/**"),
                                antMatcher("/swagger-resources/**"),
                                antMatcher("/v3/api-docs/**"),
                                antMatcher("/swagger-ui/**"),
                                antMatcher("/swagger"),
                                antMatcher("/error"),
                                antMatcher("/swagger-ui.html"))
                        .permitAll()
                        .requestMatchers(antMatcher("/api/**"))
                        .fullyAuthenticated())
                .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()))
                .build();
    }
}
