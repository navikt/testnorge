package no.nav.testnav.proxies.fullmaktproxy.consumer;

import no.nav.testnav.proxies.fullmaktproxy.consumer.command.FakedingsGetCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FakedingsConsumer {

    private static final String FAKE_TOKENDINGS_URL = "https://fakedings.intern.dev.nav.no";
    private final WebClient webClient;

    public FakedingsConsumer() {

        this.webClient = WebClient.builder()
                .baseUrl(FAKE_TOKENDINGS_URL)
                .build();
    }

    public Mono<String> getFakeToken(String ident) {

        return new FakedingsGetCommand(webClient, ident).call();
    }
}
