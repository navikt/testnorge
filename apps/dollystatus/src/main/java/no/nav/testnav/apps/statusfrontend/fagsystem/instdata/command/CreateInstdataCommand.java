package no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataRecord;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateInstdataCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final String environment;
    private final InstdataRecord record;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/inst/api/v1/institusjonsopphold/person")
                        .queryParam("environments", environment)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(record)
                .retrieve()
                .toBodilessEntity()
                .then()
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
