package no.nav.registre.testnorge.hodejegerenproxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
    private final List<String> acceptedAudience;

    public SecurityConfig(
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
            @Value("${spring.security.oauth2.resourceserver.jwt.accepted-audience}") List<String> acceptedAudience
    ) {
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.acceptedAudience = acceptedAudience;
    }


    public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            var error = new OAuth2Error("invalid_token", String.format("None of required audience values '%s' found in token", acceptedAudience), null);
            return jwt.getAudience().stream().anyMatch(acceptedAudience::contains)
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(error);
        }
    }


    private ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder jwtDecoder = (NimbusReactiveJwtDecoder)
                ReactiveJwtDecoders.fromOidcIssuerLocation(oAuth2ResourceServerProperties.getJwt().getIssuerUri());

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
                .authorizeExchange()
                .pathMatchers("/internal/isReady", "/internal/isAlive").permitAll()
                .anyExchange().permitAll()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtDecoder(jwtDecoder());
        return http.build();
    }

}

