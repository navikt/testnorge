package no.nav.testnav.apps.oversiktfrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oversiktfrontend.config.Consumers;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetApplicationAccessCommand;
import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppTilgangAnalyseConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public AppTilgangAnalyseConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavAppTilgangAnalyseService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<Application> getApplications() {
        return Flux.concat(
                getApplications(getCommand("testnorge"))
        );
    }

    private Flux<Application> getApplications(
            Function<AccessToken, GetApplicationAccessCommand> getCommand
    ) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> getCommand.apply(accessToken).call())
                .map(access -> access
                        .getAccessTo()
                        .stream()
                        .map(Application::new)
                        .collect(Collectors.toList())
                ).flatMapMany(Flux::fromIterable);
    }

    private Function<AccessToken, GetApplicationAccessCommand> getCommand(String repo) {
        return (accessToken -> new GetApplicationAccessCommand(
                webClient,
                accessToken.getTokenValue(),
                "testnav-oversikt-frontend",
                repo
        ));
    }
}
