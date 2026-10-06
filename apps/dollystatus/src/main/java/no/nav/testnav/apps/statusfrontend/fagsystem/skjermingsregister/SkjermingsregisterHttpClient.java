package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SkjermingsregisterFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command.CreateSkjermingsregisterCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command.GetSkjermingsregisterCommand;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command.UpdateSkjermingsregisterCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class SkjermingsregisterHttpClient implements SkjermingsregisterClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final SkjermingsregisterFunctionalTestProperties properties;
    private final WebClient webClient;

    public SkjermingsregisterHttpClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            SkjermingsregisterFunctionalTestProperties properties,
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
    public Mono<SkjermingsregisterResourceStatus> getScreening(
            SkjermingsregisterRequest expectedRequest,
            LocalDateTime referenceTime
    ) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new GetSkjermingsregisterCommand(
                        webClient,
                        token.getTokenValue(),
                        expectedRequest,
                        referenceTime,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> createScreening(SkjermingsregisterRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new CreateSkjermingsregisterCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        properties.getRequestTimeout()).call());
    }

    @Override
    public Mono<Void> updateScreening(SkjermingsregisterRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new UpdateSkjermingsregisterCommand(
                        webClient,
                        token.getTokenValue(),
                        request,
                        properties.getRequestTimeout()).call());
    }
}
