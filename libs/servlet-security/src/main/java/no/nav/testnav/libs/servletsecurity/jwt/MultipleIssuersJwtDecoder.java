package no.nav.testnav.libs.servletsecurity.jwt;

import com.nimbusds.jwt.JWTParser;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
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
        decoderMap = properties
                .stream()
                .collect(Collectors.toMap(
                        ResourceServerProperties::getIssuerUri,
                        MultipleIssuersJwtDecoder::getValidatingDecoder
                ));
    }

    private static NimbusJwtDecoder getValidatingDecoder(ResourceServerProperties properties) {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(properties.getIssuerUri());
        jwtDecoder.setJwtValidator(oAuth2TokenValidator(properties));
        return jwtDecoder;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {

            var issuer = JWTParser
                    .parse(token)
                    .getJWTClaimsSet()
                    .getIssuer();
            if (issuer == null || !decoderMap.containsKey(issuer)) {
                throw new JwtException("JWT decoder for issuer %s not found".formatted(issuer));
            }
            var decoder = decoderMap.get(issuer);
            log.info("Decoding token with issuer {} using decoder {}", issuer, decoder.getClass().getSimpleName());
            return decoder.decode(token);

        } catch (ParseException e) {
            log.error("Error in offset {} when parsing token", e.getErrorOffset(), e);
            throw new JwtException("Feil ved parsing av token", e);
        } catch (JwtValidationException e) {
            log.error("Error(s) validating token: {}", e.getErrors(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected failure", e);
            throw new JwtException("Unexpected failure", e);
        }
    }

    private static OAuth2TokenValidator<Jwt> oAuth2TokenValidator(ResourceServerProperties properties) {
        return new DelegatingOAuth2TokenValidator<>(
                issuerValidator(properties.getIssuerUri()),
                audienceValidator(properties.getAcceptedAudience())
        );
    }

    private static OAuth2TokenValidator<Jwt> issuerValidator(String issuerUri) {
        return JwtValidators.createDefaultWithIssuer(issuerUri); // Note that this creates and adds a default audience validator.
    }

    private static OAuth2TokenValidator<Jwt> audienceValidator(List<String> acceptedAudience) {
        return token -> {
            var audience = token.getAudience();
            var audienceIsAccepted = audience
                    .stream()
                    .anyMatch(acceptedAudience::contains);
            if (audienceIsAccepted) {
                log.info("Token audience {} is accepted by {}", audience, acceptedAudience);
                return OAuth2TokenValidatorResult.success();
            }
            var message = "Token audience %s is not accepted by %s".formatted(audience, acceptedAudience);
            log.warn(message);
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", message, null));
        };
    }

}
