package no.nav.testnav.apps.oversiktfrontend.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.testnav.apps.oversiktfrontend.consumer.command.GetApplicationAccessCommand;
import no.nav.testnav.apps.oversiktfrontend.credentials.AppTilgangAnalyseServiceProperties;
import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.libs.reactivesessionsecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class AppTilgangAnalyseConsumer {
    private final WebClient webClient;
    private final AppTilgangAnalyseServiceProperties appTilgangAnalyseServiceProperties;
    private final TokenExchange tokenExchange;

    public AppTilgangAnalyseConsumer(AppTilgangAnalyseServiceProperties appTilgangAnalyseServiceProperties, TokenExchange tokenExchange) {
        this.appTilgangAnalyseServiceProperties = appTilgangAnalyseServiceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(appTilgangAnalyseServiceProperties.getUrl())
                .build();
    }

    public Flux<Application> getApplications(ServerWebExchange serverWebExchange) {
        return Flux.concat(
                getApplications(serverWebExchange, getCommand("testnorge"))
        );
    }

    private Flux<Application> getApplications(
            ServerWebExchange serverWebExchange,
            Function<AccessToken, GetApplicationAccessCommand> getCommand
    ) {
        return tokenExchange
                .generateToken(appTilgangAnalyseServiceProperties, serverWebExchange)
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
