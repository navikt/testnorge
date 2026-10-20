package no.nav.testnav.apps.statusfrontend.fagsystem.skattekort;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SkattekortFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.command.CreateSkattekortCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.command.GetSkattekortCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SkattekortProxyHttpClient implements SkattekortClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final SkattekortFunctionalTestProperties properties;
    private final WebClient webClient;

    public SkattekortProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            SkattekortFunctionalTestProperties properties,
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
    public Mono<SkattekortResourceStatus> getTaxCard(
            FunctionalTestEnvironment environment,
            RunId runId,
            int incomeYear
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetSkattekortCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        incomeYear,
                        environment,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createTaxCard(
            FunctionalTestEnvironment environment,
            RunId runId,
            SkattekortRequest request
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateSkattekortCommand(
                        webClient,
                        token.getTokenValue(),
                        environment,
                        request,
                        properties.getRequestTimeout()).call());
    }
}
