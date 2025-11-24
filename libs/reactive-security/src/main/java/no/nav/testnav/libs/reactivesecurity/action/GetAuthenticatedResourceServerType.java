package no.nav.testnav.libs.reactivesecurity.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAuthenticatedResourceServerType extends JwtResolver implements Callable<Mono<ResourceServerType>> {

    private final List<ResourceServerProperties> resourceServerProperties;

    private Optional<ResourceServerType> getResourceTypeForm(JwtAuthenticationToken token) {
        return resourceServerProperties
                .stream()
                .filter(properties -> {
                    if (token == null) {
                        log.error("Token is null");
                    }
                    if (token.getToken() == null) {
                        log.error("Token.getToken() is null");
                    }
                    if (token.getToken().getIssuer() == null) {
                        log.error("Token.getToken().getIssuer() is null");
                    }
                    log.info("Configured issuer, token issuer: {}, {}", properties.getIssuerUri(), token.getToken().getIssuer().toString());
                    return properties
                            .getIssuerUri()
                            .equalsIgnoreCase(token.getToken().getIssuer().toString());
                })
                .findFirst()
                .map(ResourceServerProperties::getType);
    }

    @Override
    public Mono<ResourceServerType> call() {

        return getJwtAuthenticationToken()
                .onErrorResume(JwtResolverException.class, throwable -> Mono.empty())
                .flatMap(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationTokentoken) {
                        return getResourceTypeForm(jwtAuthenticationTokentoken)
                                .map(Mono::just)
                                .orElseGet(Mono::empty);
                    } else if (authentication instanceof OAuth2AuthenticationToken) {
                        return Mono.just(ResourceServerType.TOKEN_X);
                    }
                    return Mono.empty();
                });
    }
}
