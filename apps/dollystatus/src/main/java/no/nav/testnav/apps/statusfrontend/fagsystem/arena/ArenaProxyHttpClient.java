package no.nav.testnav.apps.statusfrontend.fagsystem.arena;

import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.ArenaFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.command.CreateArenaUserCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.command.DeactivateArenaUserCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.command.GetArenaUserCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ArenaProxyHttpClient implements ArenaClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final ArenaFunctionalTestProperties properties;
    private final WebClient webClient;

    public ArenaProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            ArenaFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavDollyProxy();
        this.pdlProperties = pdlProperties;
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<ArenaResourceStatus> getUser(
            FunctionalTestEnvironment environment,
            RunId runId,
            ArenaRequest expectedRequest
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetArenaUserCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        environment.name().toLowerCase(),
                        expectedRequest,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createUser(
            FunctionalTestEnvironment environment,
            RunId runId,
            ArenaRequest request
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateArenaUserCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        runId.value().toString(),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deactivateUser(FunctionalTestEnvironment environment, RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeactivateArenaUserCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        environment.name().toLowerCase(),
                        runId.value().toString(),
                        properties.getRequestTimeout()).call());
    }
}
