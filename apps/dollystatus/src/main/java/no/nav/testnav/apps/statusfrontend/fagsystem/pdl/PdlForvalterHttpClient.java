package no.nav.testnav.apps.statusfrontend.fagsystem.pdl;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command.CreatePdlForvalterPersonCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command.DeletePdlForvalterPersonCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command.GetPdlForvalterPersonCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command.SendPdlOrderCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command.UpdatePdlForvalterPersonCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PdlForvalterHttpClient implements PdlForvalterClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final PdlFunctionalTestProperties properties;
    private final WebClient webClient;

    public PdlForvalterHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.serverProperties = consumers.getTestnavPdlForvalter();
        this.properties = properties;
        this.webClient = webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Override
    public Mono<Boolean> personExists() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetPdlForvalterPersonCommand(
                        webClient,
                        token.getTokenValue(),
                        properties.getIdent(),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createPerson() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreatePdlForvalterPersonCommand(
                        webClient,
                        token.getTokenValue(),
                        properties.getIdent(),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> updateName() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new UpdatePdlForvalterPersonCommand(
                        webClient,
                        token.getTokenValue(),
                        properties.getIdent(),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<PdlOrderResponse> sendOrder() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new SendPdlOrderCommand(
                        webClient,
                        token.getTokenValue(),
                        properties.getIdent(),
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> deletePerson() {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new DeletePdlForvalterPersonCommand(
                        webClient,
                        token.getTokenValue(),
                        properties.getIdent(),
                        properties.getRequestTimeout()).call());
    }
}
