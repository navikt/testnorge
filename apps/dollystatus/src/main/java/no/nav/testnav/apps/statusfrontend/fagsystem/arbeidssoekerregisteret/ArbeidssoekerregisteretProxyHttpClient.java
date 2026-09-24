package no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret;

import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.ArbeidssoekerregisteretFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.command.CreateArbeidssoekerregistreringCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.command.DeleteArbeidssoekerregistreringCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.command.GetArbeidssoekerregistreringCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ArbeidssoekerregisteretProxyHttpClient implements ArbeidssoekerregisteretClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final ArbeidssoekerregisteretFunctionalTestProperties properties;
    private final WebClient webClient;

    public ArbeidssoekerregisteretProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            ArbeidssoekerregisteretFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavArbeidssoekerregisteretProxy();
        this.pdlProperties = pdlProperties;
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<ArbeidssoekerregisteretResourceStatus> getRegistration(
            RunId runId,
            ArbeidssoekerregisteretRequest expectedRequest
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetArbeidssoekerregistreringCommand(
                        webClient,
                        token.getTokenValue(),
                        expectedRequest,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createRegistration(RunId runId, ArbeidssoekerregisteretRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateArbeidssoekerregistreringCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteRegistration(RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteArbeidssoekerregistreringCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        properties.getRequestTimeout()).call());
    }
}
