package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class AdresseServiceCommand implements Callable<Mono<VegadresseDTO[]>> {

    private static final String VEGADRESSE_URL = "/api/v1/adresser/veg";

    private final WebClient webClient;
    private final String query;
    private final String token;

    @Override
    public Mono<VegadresseDTO[]> call() {
        return webClient
                .get()
                .uri(builder -> builder.path(VEGADRESSE_URL).query(query).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(VegadresseDTO[].class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
