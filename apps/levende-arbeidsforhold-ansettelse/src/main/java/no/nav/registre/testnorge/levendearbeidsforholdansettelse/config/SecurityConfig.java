package no.nav.registre.testnorge.levendearbeidsforholdansettelse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    @SuppressWarnings("java:S4502")
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeConfig -> authorizeConfig.requestMatchers(
                                "/internal/**",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger",
                                "/error",
                                "/swagger-ui.html",
                                "/h2/**",
                                "/member/**")
                        .permitAll()
                        .requestMatchers("/api/**")
                        .fullyAuthenticated())
                .oauth2ResourceServer(oauth2RSConfig -> oauth2RSConfig.jwt(Customizer.withDefaults()));

        return httpSecurity.build();
    }
}

