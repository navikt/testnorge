package no.nav.testnav.apps.statusfrontend.fagsystem.udi;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.UdiFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.command.CreateUdiPersonCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.command.DeleteUdiPersonCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.command.GetUdiPersonCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UdiHttpClient implements UdiClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final UdiFunctionalTestProperties properties;
    private final WebClient webClient;

    public UdiHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            UdiFunctionalTestProperties properties,
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
    public Mono<UdiResourceStatus> getPerson(UdiRequest expectedRequest) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetUdiPersonCommand(
                        webClient,
                        token.getTokenValue(),
                        expectedRequest,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createPerson(UdiRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateUdiPersonCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deletePerson(String ident) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeleteUdiPersonCommand(
                        webClient,
                        token.getTokenValue(),
                        ident,
                        properties.getRequestTimeout()).call());
    }
}
