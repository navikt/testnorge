package no.nav.testnav.apps.statusfrontend.fagsystem.nom;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.NomFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.command.CloseNomResourceCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.command.CreateNomResourceCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.command.GetNomResourceCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class NomProxyHttpClient implements NomClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final NomFunctionalTestProperties properties;
    private final WebClient webClient;

    public NomProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            NomFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavNomProxy();
        this.pdlProperties = pdlProperties;
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<NomResourceStatus> getResource(RunId runId, NomRequest expectedRequest) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetNomResourceCommand(
                        webClient,
                        token.getTokenValue(),
                        expectedRequest,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createResource(RunId runId, NomRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateNomResourceCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> closeResource(RunId runId, LocalDate endDate) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CloseNomResourceCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        endDate,
                        properties.getRequestTimeout()).call());
    }
}
