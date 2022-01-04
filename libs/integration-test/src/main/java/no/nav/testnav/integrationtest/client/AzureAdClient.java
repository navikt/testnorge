package no.nav.testnav.integrationtest.client;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.testnav.integrationtest.client.command.GenerateAzureAdTokenCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;

public class AzureAdClient {
    private final WebClient webClient;

    public AzureAdClient() {
        this.webClient = WebClient.builder().baseUrl("http://localhost:9003").build();
    }

    public Mono<AccessToken> generateToken(String audience, String oid, String sub) {
        return new GenerateAzureAdTokenCommand(webClient, oid, sub, audience).call();
    }
}
