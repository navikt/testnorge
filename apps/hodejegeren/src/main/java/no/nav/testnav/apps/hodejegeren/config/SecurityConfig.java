package no.nav.testnav.apps.hodejegeren.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Remove this call with AzureAd config
 */
@Slf4j
@Configuration
@Order(1)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrfSpec -> csrfSpec.disable())
                .authorizeHttpRequests(authorizeConfig -> authorizeConfig.requestMatchers(
                        "/internal/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger",
                        "/error",
                        "/swagger-ui.html"
                ).permitAll().requestMatchers("/api/**").fullyAuthenticated())
                .oauth2ResourceServer(oauth2RSConfig -> oauth2RSConfig.jwt(Customizer.withDefaults()));

        return httpSecurity.build();
    }
}
