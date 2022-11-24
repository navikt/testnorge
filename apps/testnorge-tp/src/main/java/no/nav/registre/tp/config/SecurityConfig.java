package no.nav.registre.tp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Remove this call with AzureAd config
 */
@Slf4j
@Configuration
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/**")
                .fullyAuthenticated()
                .and().oauth2ResourceServer().jwt();
    }

//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http
//                .httpBasic()
//                .and()
//                .headers().frameOptions().disable()
//                .and()
//                .csrf().disable()
//                .formLogin().disable();
//    }
}
