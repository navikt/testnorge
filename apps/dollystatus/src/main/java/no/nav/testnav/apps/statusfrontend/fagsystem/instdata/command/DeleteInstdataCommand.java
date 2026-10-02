package no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataSearchRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class DeleteInstdataCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final InstdataSearchRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/inst/api/v1/institusjonsopphold/person/slett")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .then()
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
