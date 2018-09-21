package no.nav.identpool;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import no.nav.freg.security.oidc.auth.common.HttpSecurityConfigurer;

@Configuration
public class SecurityConfig implements HttpSecurityConfigurer {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/identifikator/v1/finneshosskatt").authenticated()
                .antMatchers("/**").permitAll()
                .and()
                .csrf().disable();
    }
}