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
        this.webClient = WebClient
                .builder()
                .baseUrl(appTilgangAnalyseServiceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Flux<String> getScopes(ServerWebExchange serverWebExchange) {
        return Flux.concat(
                getScopes(serverWebExchange, getCommand("dolly-backend")),
                getScopes(serverWebExchange, getCommand("testnorge"))
        );
    }

    public Flux<String> getScopes(ServerWebExchange serverWebExchange, Function<AccessToken, GetApplicationAccessCommand> getCommand) {
        return tokenExchange
                .generateToken(appTilgangAnalyseServiceProperties, serverWebExchange)
                .flatMap(accessToken -> getCommand.apply(accessToken).call())
                .map(access -> access
                        .getAccessTo()
                        .stream()
                        .map(application -> application.getCluster() + "." + application.getNamespace() + "." + application.getName())
                        .collect(Collectors.toList())
                ).flatMapMany(Flux::fromIterable);
    }

    public Function<AccessToken, GetApplicationAccessCommand> getCommand(String repo) {
        return (accessToken -> new GetApplicationAccessCommand(webClient, accessToken.getTokenValue(), "testnav-oversikt-frontend", repo));
    }
}
