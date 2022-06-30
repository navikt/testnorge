package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.fastedata;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.fastedata.Organisasjon;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;

@RequiredArgsConstructor
public class GetOrganisasjonerCommand implements Callable<Mono<List<Organisasjon>>> {
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<Organisasjon>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<Organisasjon>> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/v1/organisasjoner")
                        .queryParam("gruppe", "DOLLY")
                        .queryParam("kanHaArbeidsforhold", "true")
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));

    }
}
