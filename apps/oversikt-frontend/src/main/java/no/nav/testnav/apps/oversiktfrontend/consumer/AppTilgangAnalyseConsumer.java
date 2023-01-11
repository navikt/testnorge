package no.nav.testnav.apps.oversiktfrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.token_client.client.AzureAdOnBehalfOfTokenClient;
import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetApplicationAccessCommand;
import no.nav.testnav.apps.oversiktfrontend.credentials.AppTilgangAnalyseServiceProperties;
import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppTilgangAnalyseConsumer {
    private final WebClient webClient;
    private final AppTilgangAnalyseServiceProperties appTilgangAnalyseServiceProperties;
    private final AzureAdOnBehalfOfTokenClient tokenExchange;

    public AppTilgangAnalyseConsumer(AppTilgangAnalyseServiceProperties appTilgangAnalyseServiceProperties,
                                     AzureAdOnBehalfOfTokenClient tokenExchange,
                                     ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.appTilgangAnalyseServiceProperties = appTilgangAnalyseServiceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(appTilgangAnalyseServiceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Flux<Application> getApplications(ServerWebExchange serverWebExchange) {
        return Flux.concat(
                getApplications(serverWebExchange, getCommand("testnorge"))
        );
    }

    private Flux<Application> getApplications(
            ServerWebExchange serverWebExchange, Function<String, GetApplicationAccessCommand> getCommand
    ) {
        return
                getCommand.apply(tokenExchange
                                .exchangeOnBehalfOfToken(appTilgangAnalyseServiceProperties.toAzureAdScope(), serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))).call()
                        .map(access -> access
                                .getAccessTo()
                                .stream()
                                .map(Application::new)
                                .collect(Collectors.toList())
                        ).flatMapMany(Flux::fromIterable);
    }

    private Function<String, GetApplicationAccessCommand> getCommand(String repo) {
        return (accessToken -> new GetApplicationAccessCommand(
                webClient,
                accessToken,
                "testnav-oversikt-frontend",
                repo
        ));
    }
}
