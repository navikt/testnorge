package no.nav.testnav.libs.reactivesessionsecurity.action;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesessionsecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
public class GetAuthenticatedResourceServerType extends JwtResolver implements Callable<Mono<ResourceServerType>> {

    private final List<ResourceServerProperties> resourceServerProperties;

    private Optional<ResourceServerType> getResourceTypeForm(JwtAuthenticationToken token) {
        return resourceServerProperties
                .stream()
                .filter(properties -> properties
                        .getIssuerUri()
                        .equalsIgnoreCase(token.getToken().getIssuer().toString()))
                .findFirst()
                .map(ResourceServerProperties::getType);
    }

    @Override
    public Mono<ResourceServerType> call() {
        return getJwtAuthenticationToken()
                .onErrorResume(JwtResolverException.class, throwable -> Mono.empty())
                .flatMap(token -> getResourceTypeForm(token)
                        .map(Mono::just)
                        .orElseGet(Mono::empty)
                );
    }
}
