package no.nav.testnav.apps.statusfrontend.fagsystem.instdata;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.InstdataFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command.CreateInstdataCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command.DeleteInstdataCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command.GetInstdataCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command.GetInstdataEnvironmentsCommand;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class InstdataProxyHttpClient implements InstdataClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final InstdataFunctionalTestProperties properties;
    private final WebClient webClient;

    public InstdataProxyHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            InstdataFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavDollyProxy();
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<InstdataEnvironments> getEnvironments(RunId runId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetInstdataEnvironmentsCommand(
                        webClient,
                        token.getTokenValue(),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<InstdataResourceStatus> getInstdata(
            RunId runId,
            String ident,
            String environment,
            InstdataRecord expectedRecord
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetInstdataCommand(
                        webClient,
                        token.getTokenValue(),
                        new InstdataSearchRequest(ident, List.of(environment)),
                        expectedRecord,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createInstdata(RunId runId, String environment, InstdataRecord record) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateInstdataCommand(
                        webClient,
                        token.getTokenValue(),
                        environment,
                        record,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deleteInstdata(RunId runId, String ident, List<String> environments) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteInstdataCommand(
                        webClient,
                        token.getTokenValue(),
                        new InstdataSearchRequest(ident, environments),
                        properties.getRequestTimeout()).call());
    }
}
