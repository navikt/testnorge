package no.nav.registre.aareg.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import no.nav.freg.security.oidc.auth.common.HttpSecurityConfigurer;

@Slf4j
@Configuration
@Order(1)
public class SecurityConfig implements HttpSecurityConfigurer {

    @Value("${testnorge.aareg.security.cors.origins:''}")
    private String[] allowedOrigins;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .csrf().disable()
                .formLogin().disable();
    }
}
