package no.nav.registre.testnorge.oversiktfrontend.router;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.oversiktfrontend.config.ApplicationsProperties;
import no.nav.registre.testnorge.oversiktfrontend.router.dto.TokenDTO;
import no.nav.testnav.libs.security.domain.AccessScopes;
import no.nav.testnav.libs.security.service.AccessTokenService;

@Component
@RequiredArgsConstructor
public class ApplicationHandler {

    private final ApplicationsProperties properties;
    private final AccessTokenService accessTokenService;

    public Mono<ServerResponse> getApplications(ServerRequest request) {
        var applications = properties.getApplications().keySet();
        return ServerResponse.ok().body(BodyInserters.fromValue(applications));
    }

    public Mono<ServerResponse> onBehalfOf(ServerRequest request) {
        var scope = properties.getApplications().get(request.pathVariable("name"));

        if (scope == null) {
            return ServerResponse.notFound().build();
        }

        return accessTokenService
                .generateToken(new AccessScopes("api://" + scope + "/.default"))
                .flatMap(token -> ServerResponse.ok().body(BodyInserters.fromValue(new TokenDTO(token))));
    }
}
