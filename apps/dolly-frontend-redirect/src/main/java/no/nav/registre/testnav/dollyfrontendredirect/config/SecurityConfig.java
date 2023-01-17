package no.nav.registre.testnav.dollyfrontendredirect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Order(1)
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeRequests()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .oauth2Client()
                .and()
                .oauth2Login()
                .and()
                .csrf()
                .disable()
                .logout()
                .logoutSuccessUrl("/");

        return httpSecurity.build();
    }
}

