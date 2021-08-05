package no.nav.testnav.libs.servletsecurity.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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

public abstract class OAuth2ResourceServerConfiguration extends WebSecurityConfigurerAdapter {

    private final List<String> acceptedAudience;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    /**
     * The property spring.security.oauth2.resourceserver.jwt.accepted-audience used in @Value annotation is not
     * a default spring property, but is added as a custom property here
     */
    public OAuth2ResourceServerConfiguration(OAuth2ResourceServerProperties oAuth2ResourceServerProperties, List<String> acceptedAudience) {
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.acceptedAudience = acceptedAudience;
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

