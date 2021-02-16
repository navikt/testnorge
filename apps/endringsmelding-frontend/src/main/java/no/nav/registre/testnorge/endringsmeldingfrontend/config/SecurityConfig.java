package no.nav.registre.testnorge.endringsmeldingfrontend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Slf4j
@Configuration
@Order(1)
@Profile({"dev", "prod"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .oauth2Client()
                .and()
                .oauth2Login(o -> o.loginPage("/login/oauth2/code/aad").failureUrl("/page/login?error=true"))
                .csrf()
                .disable();
    }
}