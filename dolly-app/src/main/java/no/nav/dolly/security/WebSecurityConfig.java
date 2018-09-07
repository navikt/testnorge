package no.nav.dolly.security;

import lombok.extern.slf4j.Slf4j;
import no.nav.freg.security.oidc.auth.OidcWebSecurityConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

@Slf4j
@Configuration
@Order(1)
public class WebSecurityConfig extends OidcWebSecurityConfig {

    @Override
    public void configure(WebSecurity web) {
//        web.ignoring().antMatchers(HttpMethod.GET);
//        web.ignoring().antMatchers(HttpMethod.POST,"/api/v1/kompetansekoe/findby", "/api/v1/arbeidsfordeling", "/api/v1/arbeidsfordelinger", "/api/v1/enhet/kontaktinformasjoner");
    }
}
