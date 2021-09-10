package no.nav.testnav.apps.oversiktfrontend.router;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.config.ApplicationsProperties;
import no.nav.testnav.apps.oversiktfrontend.router.dto.TokenDTO;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;

@Component
@RequiredArgsConstructor
public class ApplicationHandler {

    private final ApplicationsProperties properties;
    private final AzureAdTokenExchange azureAdTokenExchange;

    public Mono<ServerResponse> getApplications(ServerRequest request) {
        var applications = properties.getApplications().keySet();
        return ServerResponse.ok().body(BodyInserters.fromValue(applications));
    }

    public Mono<ServerResponse> onBehalfOf(ServerRequest request) {
        var scope = properties.getApplications().get(request.pathVariable("name"));

        if (scope == null) {
            return ServerResponse.notFound().build();
        }

        return azureAdTokenExchange
                .generateToken("api://" + scope + "/.default", request.exchange())
                .flatMap(token -> ServerResponse.ok().body(BodyInserters.fromValue(new TokenDTO(token))));
    }
}
