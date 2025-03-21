package no.nav.skattekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.skattekortservice.dto.SokosRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SokosPostCommand implements Callable<Mono<String>> {

    private static final String SOKOS_URL = "/api/v1/skattekort/opprett";

    private final WebClient webClient;
    private final SokosRequest request;
    private final String token;

    @Override
    public Mono<String> call() {
        return webClient
                .post()
                .uri(SOKOS_URL)
                .headers(WebClientHeader.bearer(token))
                .header("korrelasjonsid", UUID.randomUUID().toString())
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}