package no.nav.testnav.libs.servletsecurity.action;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.servletsecurity.properties.ResourceServerProperties;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;


@Component
@RequiredArgsConstructor
public class GetAuthenticatedResourceServerType extends JwtResolver implements Callable<ResourceServerType> {

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
    public ResourceServerType call() {
        var token = getJwtAuthenticationToken();
        return getResourceTypeForm(token).orElseThrow();
    }
}
