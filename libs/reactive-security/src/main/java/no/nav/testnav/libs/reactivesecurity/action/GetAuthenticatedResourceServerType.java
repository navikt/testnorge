package no.nav.testnav.libs.reactivesecurity.action;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.domain.ResourceServerType;

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
                .flatMap(token -> getResourceTypeForm(token)
                        .map(Mono::just)
                        .orElseGet(Mono::empty)
                );
    }
}
