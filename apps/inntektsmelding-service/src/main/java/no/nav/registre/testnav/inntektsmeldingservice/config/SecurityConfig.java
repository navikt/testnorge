package no.nav.registre.testnav.inntektsmeldingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.List;

import no.nav.testnav.libs.servletsecurity.config.OAuth2ResourceServerConfiguration;

@EnableWebSecurity
@Configuration
@Profile({"prod", "dev"})
public class SecurityConfig extends OAuth2ResourceServerConfiguration {

    public SecurityConfig(
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
            @Value("${spring.security.oauth2.resourceserver.jwt.accepted-audience}") List<String> acceptedAudience
    ) {
        super(oAuth2ResourceServerProperties, acceptedAudience);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/**")
                .fullyAuthenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .decoder(jwtDecoder());
    }
}