package no.nav.registre.orkestratoren.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.List;

import no.nav.testnav.libs.servletsecurity.config.OAuth2ResourceServerConfiguration;


@Order(1)
@EnableWebSecurity
@Configuration
@Profile({"prod", "local"})
public class SecurityConfig extends OAuth2ResourceServerConfiguration {

    public SecurityConfig(
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
            @Value("${spring.security.oauth2.resourceserver.jwt.accepted-audience}") List<String> acceptedAudience
    ) {
        super(oAuth2ResourceServerProperties, acceptedAudience);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/api/**")
                .fullyAuthenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .decoder(jwtDecoder());
    }
}