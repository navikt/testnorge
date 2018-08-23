package no.nav.dolly.security;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@Order(1)
public class WebSecurityConfig /*extends OidcWebSecurityConfigurerAdapter*/ {

//    @Override
//    public void configure(WebSecurity web) {
//        //web.ignoring().antMatchers(HttpMethod.GET);
//    }
}
