package no.nav.testnav.libs.standalone.servletsecurity.jwt;

import com.nimbusds.jwt.JWTParser;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.standalone.servletsecurity.properties.ResourceServerProperties;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
class MultipleIssuersJwtDecoder implements JwtDecoder {

    private final Map<String, NimbusJwtDecoder> decoderMap;

    MultipleIssuersJwtDecoder(List<ResourceServerProperties> properties) {
        this.decoderMap = properties.stream().collect(Collectors.toMap(
                ResourceServerProperties::getIssuerUri,
                props -> {
                    NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(props.getIssuerUri());
                    jwtDecoder.setJwtValidator(oAuth2TokenValidator(props));
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
            log.error("Feil ved validering av token", e);
            throw e;
        } catch (Exception e) {
            log.error("Ukjent feil", e);
            throw e;
        }
    }

    private OAuth2TokenValidator<Jwt> oAuth2TokenValidator(ResourceServerProperties properties) {
        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(properties.getIssuerUri());
        OAuth2TokenValidator<Jwt> audienceValidator = token ->
                token.getAudience().stream().anyMatch(audience -> properties.getAcceptedAudience().contains(audience)) ?
                        OAuth2TokenValidatorResult.success() :
                        OAuth2TokenValidatorResult.failure(createError(
                                String.format("Fant ikke påkrevd audience %s i tokenet.", properties.getAcceptedAudience())
                        ));
        return new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator);
    }

    private OAuth2Error createError(String msg) {
        return new OAuth2Error("invalid_token", msg, null);
    }

}
