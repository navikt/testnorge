package no.nav.testnav.libs.reactivesecurity.manager;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.SneakyThrows;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * hentet fra org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
 */
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final GetJwtDecoder getJwtDecoder;

    private final Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter =
            new ReactiveJwtAuthenticationConverterAdapter(new JwtAuthenticationConverter());

    public JwtReactiveAuthenticationManager(
            WebClient webClient,
            List<ResourceServerProperties> resourceServerProperties,
            String proxy
    ) {
        var jwtDecoders = resourceServerProperties
                .stream()
                .collect(Collectors.toMap(
                        ResourceServerProperties::getIssuerUri,
                        props -> new NonBeanJwtDecoder(webClient, props, proxy).jwtDecoder(),
                        (first, _) -> first));

        this.getJwtDecoder = jwt -> {
            var issuer = getIssuer(jwt);
            var jwtDecoder = jwtDecoders.get(issuer);
            if (isNull(jwtDecoder)) {
                throw new AuthenticationServiceException("Finner ikke støtte for issuer " + issuer);
            }
            return jwtDecoder;
        };
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(BearerTokenAuthenticationToken.class::isInstance)
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

    @SneakyThrows
    private static JWT parse(String token) {
        return JWTParser.parse(token);
    }

    @SneakyThrows
    private static String getIssuer(JWT jwt) {
        return jwt.getJWTClaimsSet().getIssuer();
    }

}

