package no.nav.registre.testnorge.arbeidsforholdservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Order(1)
@EnableWebSecurity
@Configuration
@Profile({ "prod", "dev" })
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
                .oauth2ResourceServer(oauth2RSConfig -> oauth2RSConfig.jwt(jwtConfigurer -> {
                }));

        return httpSecurity.build();
    }
}

