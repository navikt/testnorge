package no.nav.testnav.apps.persontilgangservice.client.altinn.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

import no.nav.testnav.apps.persontilgangservice.client.altinn.v1.dto.AccessDTO;
import reactor.util.retry.Retry;

@Slf4j
@RequiredArgsConstructor
public class GetPersonAccessCommand implements Callable<Mono<AccessDTO[]>> {
    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String serviceCode;
    private final String serviceEdition;
    private final String apiKey;

    @Override
    public Mono<AccessDTO[]> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/serviceowner/reportees")
                        .queryParam("subject", ident)
                        .queryParam("serviceCode", serviceCode)
                        .queryParam("serviceEdition", serviceEdition)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ApiKey", apiKey)
                .retrieve()
                .bodyToMono(AccessDTO[].class)
                .doOnError(error -> log.error("Henting av \"/reportees\" feilet {}", error.getMessage(), error))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
