package no.nav.testnav.apps.oversiktfrontend.router;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.router.dto.TokenDTO;
import no.nav.testnav.libs.reactivesecurity.domain.AccessScopes;
import no.nav.testnav.libs.reactivesecurity.service.AzureAdTokenExchange;

@Component
@RequiredArgsConstructor
public class TokenHandler {

    private final AzureAdTokenExchange azureAdTokenExchange;

    public Mono<ServerResponse> onBehalfOf(ServerRequest request) {
        var scope = request.pathVariable("scope");
        return azureAdTokenExchange
                .generateToken(new AccessScopes("api://" + scope + "/.default"))
                .flatMap(token -> ServerResponse.ok().body(BodyInserters.fromValue(new TokenDTO(token))));
    }
}
