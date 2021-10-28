package no.nav.dolly.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;

@Profile("!test")
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
    private List<String> acceptedAudience;

    public SecurityConfiguration(
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
            @Value("${spring.security.oauth2.resourceserver.jwt.accepted-audience}") List<String> acceptedAudience) {

        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.acceptedAudience = acceptedAudience;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
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

    /**
     * IMPORTANT: needed to add audience (aud claim) validation.
     * only issuer is validated by default, in addition to signature and expiry)
     */
    protected JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(
                oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        jwtDecoder.setJwtValidator(oAuth2TokenValidator());
        return jwtDecoder;
    }

    protected OAuth2TokenValidator<Jwt> oAuth2TokenValidator() {
        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(
                        oAuth2ResourceServerProperties.getJwt().getIssuerUri());

        OAuth2TokenValidator<Jwt> audienceValidator = token ->
                token.getAudience().stream().anyMatch(acceptedAudience::contains) ?
                        OAuth2TokenValidatorResult.success() :
                        OAuth2TokenValidatorResult.failure(
                                new OAuth2Error("invalid_token",
                                        String.format("None of required audience values '%s' found in token",
                                                acceptedAudience),
                                        null));

        return new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator);
    }
}