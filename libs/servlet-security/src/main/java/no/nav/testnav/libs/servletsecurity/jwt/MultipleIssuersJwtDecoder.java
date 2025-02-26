package no.nav.testnav.libs.servletsecurity.jwt;

import com.nimbusds.jwt.JWTParser;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
class MultipleIssuersJwtDecoder implements JwtDecoder {
    private final Map<String, NimbusJwtDecoder> decoderMap;

    MultipleIssuersJwtDecoder(List<ResourceServerProperties> properties) {
        this.decoderMap = properties
                .stream()
                .collect(Collectors.toMap(
                        ResourceServerProperties::getIssuerUri,
                        config -> {
                            var jwtDecoder = NimbusJwtDecoder
                                    .withIssuerLocation(config.getIssuerUri())
                                    .build();
                            jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultIssuerValidator(config), customAudienceValidator(config)));
                            return jwtDecoder;
                        }
                ));
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var issuer = JWTParser
                    .parse(token)
                    .getJWTClaimsSet()
                    .getIssuer();
            return decoderMap
                    .get(issuer)
                    .decode(token);
        } catch (ParseException e) {
            log.error("Feil ved parsing av token", e);
            throw new JwtException("Feil ved parsing av token", e);
        } catch (JwtValidationException e) {
            log.error("Feil ved validering av token: {}", e.getErrors(), e);
            throw e;
        } catch (Exception e) {
            log.error("Ukjent feil", e);
            throw e;
        }
    }

    private static OAuth2TokenValidator<Jwt> defaultIssuerValidator(ResourceServerProperties properties) {
        return JwtValidators.createDefaultWithIssuer(properties.getIssuerUri());
    }

    private static OAuth2TokenValidator<Jwt> customAudienceValidator(ResourceServerProperties properties) {
        return token -> {
            var valid = token
                    .getAudience()
                    .stream()
                    .anyMatch(audience -> properties.getAcceptedAudience().contains(audience));
            if (!valid) {
                log.warn("Fant ikke påkrevd audience {} i tokenet, bare {}", properties.getAcceptedAudience(), token.getAudience());
            }
            return OAuth2TokenValidatorResult.success();
        };
//        return token -> token
//                .getAudience()
//                .stream()
//                .anyMatch(audience -> properties.getAcceptedAudience().contains(audience)) ?
//                OAuth2TokenValidatorResult.success() :
//                OAuth2TokenValidatorResult.failure(error(properties.getAcceptedAudience(), token.getAudience()));
    }

//    private static OAuth2Error error(List<String> acceptedAudiences, List<String> tokenAudiences) {
//        var message = "Fant ikke påkrevd audience %s i tokenet, bare %s".formatted(acceptedAudiences, tokenAudiences);
//        log.error(message);
//        return new OAuth2Error("invalid_token", message, null);
//    }

}
