package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class AdresseServiceCommand implements Callable<Mono<VegadresseDTO[]>> {

    private static final String VEGADRESSE_URL = "/api/v1/adresser/veg";

    private final WebClient webClient;
    private final String query;
    private final String token;

    @Override
    public Mono<VegadresseDTO[]> call() {

        return webClient.get()
                .uri(builder -> builder.path(VEGADRESSE_URL).query(query).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(VegadresseDTO[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
