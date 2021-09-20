package no.nav.testnav.libs.reactivesecurity.manager;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import reactor.core.publisher.Mono;

import java.util.List;

import no.nav.testnav.libs.reactivesecurity.decoder.JwtDecoder;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;

/**
 * hentet fra org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
 */
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final GetJwtDecoder getJwtDecoder;

    private Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverterAdapter(
            new JwtAuthenticationConverter());

    public JwtReactiveAuthenticationManager(
            List<ResourceServerProperties> resourceServerProperties,
            String proxy
    ) {
        this.getJwtDecoder = jwt -> resourceServerProperties
                .stream()
                .filter(props -> props.getIssuerUri().equals(getIssuer(jwt)))
                .findFirst()
                .map(props -> new JwtDecoder(props, proxy).jwtDecoder())
                .orElseThrow();
    }

    @SneakyThrows
    private static JWT parse(String token) {
        return JWTParser.parse(token);
    }

    @SneakyThrows
    private static String getIssuer(JWT jwt) {
        return jwt.getJWTClaimsSet().getIssuer();
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter((a) -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap(token -> getJwtDecoder.from(parse(token)).decode(token))
                .flatMap(this.jwtAuthenticationConverter::convert)
                .cast(Authentication.class)
                .onErrorMap(JwtException.class, this::onError);
    }

    private AuthenticationException onError(JwtException ex) {
        if (ex instanceof BadJwtException) {
            return new InvalidBearerTokenException(ex.getMessage(), ex);
        }
        return new AuthenticationServiceException(ex.getMessage(), ex);
    }

}

