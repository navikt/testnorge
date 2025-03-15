package no.nav.skattekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.skattekortservice.dto.SokosResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.Callable;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
public class SokosGetCommand implements Callable<Flux<SokosResponse>> {

    private static final String SOKOS_URL = "/api/v1/skattekort/hent/{fnr}";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<SokosResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(SOKOS_URL)
                        .build(ident))
                .header(AUTHORIZATION, "Bearer " + token)
                .header("korrelasjonsid", UUID.randomUUID().toString())
                .retrieve()
                .bodyToFlux(SokosResponse.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException());
    }

}