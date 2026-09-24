package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.TpsMessagingFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command.CreateEgenansattCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command.DeleteEgenansattCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command.GetEgenansattCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public class TpsMessagingHttpClient implements TpsMessagingClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final TpsMessagingFunctionalTestProperties properties;
    private final WebClient webClient;

    public TpsMessagingHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            TpsMessagingFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavTpsMessagingService();
        this.pdlProperties = pdlProperties;
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<TpsEgenansattResourceStatus> getEgenansatt(
            RunId runId,
            List<String> environments,
            LocalDate expectedFromDate
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetEgenansattCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        environments,
                        expectedFromDate,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createEgenansatt(
            RunId runId,
            List<String> environments,
            LocalDate fromDate
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateEgenansattCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        environments,
                        fromDate,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteEgenansatt(RunId runId, List<String> environments) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteEgenansattCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        environments,
                        properties.getRequestTimeout()).call());
    }
}
