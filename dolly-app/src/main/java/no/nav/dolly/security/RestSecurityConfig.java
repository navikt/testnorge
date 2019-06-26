package no.nav.dolly.security;


import no.nav.freg.security.oidc.auth.common.HttpSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableCaching
public class RestSecurityConfig implements HttpSecurityConfigurer {

    @Value("${dolly.security.cors.origins: ''}")
    private String[] allowedOrigins;

    @Value("${environment: ''}")
    private String environment;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/v1/**").authenticated()
                .antMatchers("/**").permitAll()
                .and()
                .csrf().disable();
    }
}
