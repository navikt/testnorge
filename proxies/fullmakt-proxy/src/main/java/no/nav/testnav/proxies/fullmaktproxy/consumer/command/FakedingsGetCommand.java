package no.nav.testnav.proxies.fullmaktproxy.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class FakedingsGetCommand implements Callable<Mono<String>> {

    private static final String FAKEDINGS_URL = "/fake/idporten";
    private final WebClient webClient;
    private final String ident;

    @Override
    public Mono<String> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(FAKEDINGS_URL)
                        .queryParam("pid", ident)
                        .queryParam("acr", "Level4")
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
