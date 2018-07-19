package no.nav.security;

import lombok.extern.slf4j.Slf4j;
import no.nav.freg.security.oidc.config.OidcWebSecurityConfigurerAdapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

@Slf4j
@Configuration
@Order(1)
public class WebSecurityConfig extends OidcWebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        //web.ignoring().antMatchers(HttpMethod.GET);
    }
}
