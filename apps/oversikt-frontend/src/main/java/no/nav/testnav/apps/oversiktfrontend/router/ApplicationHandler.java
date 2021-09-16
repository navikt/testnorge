package no.nav.testnav.apps.oversiktfrontend.router;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.config.ApplicationsProperties;
import no.nav.testnav.apps.oversiktfrontend.consumer.AppTilgangAnalyseConsumer;
import no.nav.testnav.apps.oversiktfrontend.router.dto.TokenDTO;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;

@Component
@RequiredArgsConstructor
public class ApplicationHandler {

    private final AzureAdTokenExchange azureAdTokenExchange;
    private final AppTilgangAnalyseConsumer appTilgangAnalyseConsumer;

    public Mono<ServerResponse> getApplications(ServerRequest request) {
        return appTilgangAnalyseConsumer
                .getScopes(request.exchange())
                .collectList()
                .flatMap(scopes -> ServerResponse
                        .ok()
                        .body(BodyInserters.fromValue(scopes))
                );
    }

    public Mono<ServerResponse> onBehalfOf(ServerRequest request) {
        var scope = request.pathVariable("scope");
        return azureAdTokenExchange
                .generateToken("api://" + scope + "/.default", request.exchange())
                .flatMap(token -> ServerResponse.ok().body(BodyInserters.fromValue(new TokenDTO(token))));
    }
}
