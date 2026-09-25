package no.nav.testnav.apps.statusfrontend.fagsystem.krr;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.KrrFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.command.CreateKrrContactInformationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.command.DeleteKrrContactInformationCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.command.GetKrrContactInformationCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KrrProxyHttpClient implements KrrClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final KrrFunctionalTestProperties properties;
    private final WebClient webClient;

    public KrrProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            KrrFunctionalTestProperties properties,
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
    public Mono<KrrResourceStatus> getContactInformation(
            RunId runId,
            KrrRequest expectedRequest
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetKrrContactInformationCommand(
                        webClient,
                        token.getTokenValue(),
                        expectedRequest,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createContactInformation(RunId runId, KrrRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateKrrContactInformationCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteContactInformation(RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteKrrContactInformationCommand(
                        webClient,
                        token.getTokenValue(),
                        pdlProperties.getIdent(),
                        properties.getRequestTimeout()).call());
    }
}
