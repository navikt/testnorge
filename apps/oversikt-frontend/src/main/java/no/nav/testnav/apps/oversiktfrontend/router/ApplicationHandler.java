package no.nav.testnav.apps.oversiktfrontend.router;

import org.springframework.http.CacheControl;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import no.nav.testnav.apps.oversiktfrontend.consumer.AppTilgangAnalyseConsumer;
import no.nav.testnav.apps.oversiktfrontend.router.dto.TokenDTO;
import no.nav.testnav.libs.reactivesessionsecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;

@Component
public class ApplicationHandler {

    private final TokenExchange tokenExchange;
    private final AppTilgangAnalyseConsumer appTilgangAnalyseConsumer;
    private final CacheControl cacheControl;

    public ApplicationHandler(
            TokenExchange tokenExchange,
            AppTilgangAnalyseConsumer appTilgangAnalyseConsumer
    ) {
        this.tokenExchange = tokenExchange;
        this.appTilgangAnalyseConsumer = appTilgangAnalyseConsumer;
        this.cacheControl = CacheControl.maxAge(10, TimeUnit.MINUTES).noTransform().mustRevalidate();
    }

    public Mono<ServerResponse> getApplications(ServerRequest request) {
        return appTilgangAnalyseConsumer
                .getScopes(request.exchange())
                .collectList()
                .flatMap(scopes -> ServerResponse
                        .ok()
                        .cacheControl(cacheControl)
                        .body(BodyInserters.fromValue(scopes))
                );
    }

    public Mono<ServerResponse> onBehalfOf(ServerRequest request) {
        var scope = request.pathVariable("scope");
        return tokenExchange
                .generateToken(create(scope), request.exchange())
                .flatMap(token -> ServerResponse.ok().body(BodyInserters.fromValue(new TokenDTO(token))));
    }

    public ServerProperties create(String scope) {
        var parts = Arrays.stream(scope.split("\\.")).collect(Collectors.toList());
        return ServerProperties
                .builder()
                .cluster(parts.get(0))
                .namespace(parts.get(1))
                .name(parts.get(2))
                .build();
    }
}
