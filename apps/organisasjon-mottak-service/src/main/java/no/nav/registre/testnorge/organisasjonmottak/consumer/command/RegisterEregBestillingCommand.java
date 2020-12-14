package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class RegisterEregBestillingCommand implements Callable<Mono<Void>> {
    private final WebClient webClient;
    private final String token;
    private final String uuid;
    private final String miljo;
    private final Long itemId;

    @Override
    public Mono<Void> call() {
        return webClient
                .post()
                .uri(builder -> builder.path("/api/v1/ereg/batch/queue/items/{id}").build(itemId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("miljo", miljo)
                .header("uuid", uuid)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
