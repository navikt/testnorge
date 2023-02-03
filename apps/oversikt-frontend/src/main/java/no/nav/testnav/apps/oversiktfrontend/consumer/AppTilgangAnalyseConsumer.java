package no.nav.testnav.apps.oversiktfrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetApplicationAccessCommand;
import no.nav.testnav.apps.oversiktfrontend.credentials.AppTilgangAnalyseServiceProperties;
import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppTilgangAnalyseConsumer {
    private final WebClient webClient;
    private final AppTilgangAnalyseServiceProperties appTilgangAnalyseServiceProperties;
    private final TokenExchange tokenExchange;

    public AppTilgangAnalyseConsumer(AppTilgangAnalyseServiceProperties appTilgangAnalyseServiceProperties,
                                     TokenExchange tokenExchange,
                                     ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.appTilgangAnalyseServiceProperties = appTilgangAnalyseServiceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(appTilgangAnalyseServiceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
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
                .exchange(appTilgangAnalyseServiceProperties)
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
