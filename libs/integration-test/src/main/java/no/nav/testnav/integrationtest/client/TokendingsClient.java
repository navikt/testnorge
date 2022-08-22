package no.nav.testnav.integrationtest.client;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.testnav.integrationtest.client.command.GenerateTokenXTokenCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;

public class TokendingsClient {
    private final WebClient webClient;

    public TokendingsClient() {
        this.webClient = WebClient.builder().baseUrl("http://localhost:9001").build();
    }

    public Mono<AccessToken> generateToken(String audience, String pid) {
        return new GenerateTokenXTokenCommand(webClient, pid, audience).call();
    }
}
