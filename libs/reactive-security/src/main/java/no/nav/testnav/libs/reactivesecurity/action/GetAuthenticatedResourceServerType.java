package no.nav.testnav.libs.reactivesecurity.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetAuthenticatedResourceServerType extends JwtResolver implements Callable<Mono<ResourceServerType>> {

    private final List<ResourceServerProperties> resourceServerProperties;

    private Optional<ResourceServerType> getResourceTypeFrom(JwtAuthenticationToken token) {
        return resourceServerProperties
                .stream()
                .filter(properties -> {
                    if (token == null) {
                        log.warn("Token is null");
                    } else if (token.getToken() == null) {
                        log.warn("Token.getToken() is null");
                    } else if (token.getToken().getIssuer() == null) {
                        log.warn("Token.getToken().getIssuer() is null");
                    }
                    return Optional
                            .ofNullable(token)
                            .map(JwtAuthenticationToken::getToken)
                            .map(JwtClaimAccessor::getIssuer)
                            .map(issuerFromToken -> issuerFromToken.toString().equalsIgnoreCase(properties.getIssuerUri()))
                            .orElse(false);
                })
                .findFirst()
                .map(ResourceServerProperties::getType);
    }

    @Override
    public Mono<ResourceServerType> call() {

        return getJwtAuthenticationToken()
                .onErrorResume(EmptyReactiveSecurityContextException.class, exception -> {
                    log.error("Failed to get JWT token", exception);
                    return Mono.empty();
                })
                .flatMap(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken token) {
                        return Mono.justOrEmpty(getResourceTypeFrom(token));
                    }
                    if (authentication instanceof OAuth2AuthenticationToken) {
                        return Mono.just(ResourceServerType.TOKEN_X);
                    }
                    return Mono.empty();
                });

    }

}
