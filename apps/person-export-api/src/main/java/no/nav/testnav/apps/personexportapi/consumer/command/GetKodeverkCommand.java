package no.nav.testnav.apps.personexportapi.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personexportapi.consumer.response.KodeverkBetydningerResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetKodeverkCommand implements Callable<Mono<KodeverkBetydningerResponse>> {
    private final WebClient webClient;
    private final String token;
    private final String kodeverksnavn;

    protected static String getMessage(Throwable error) {
        return error instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString() :
                error.getMessage();
    }

    @Override
    public Mono<KodeverkBetydningerResponse> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger")
                        .queryParam("ekskluderUgyldige", "true")
                        .queryParam("spraak", "nb")
                        .build(kodeverksnavn)
                )
                .header("Authorization", "Bearer " + token)
                .header("Nav-Consumer-Id", "testnav-person-export-api")
                .header("Nav-Call-Id", UUID.randomUUID().toString())
                .retrieve()
                .bodyToMono(KodeverkBetydningerResponse.class)
                .onErrorResume(throwable -> {
                    log.error("Feil i henting av kodeverk: " + getMessage(throwable));
                    return Mono.just(KodeverkBetydningerResponse.builder()
                            .betydninger(Collections.emptyMap())
                            .build());
                });
    }
}
