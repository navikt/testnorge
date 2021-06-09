package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class AdresseServiceCommand implements Callable<Mono<VegadresseDTO[]>> {

    private static final String ADRESSER_VEG_URL = "/api/v1/adresser/veg";

    private final WebClient webClient;
    private final String query;
    private final String token;

    @Override
    public Mono<VegadresseDTO[]> call() {

        try {
            return webClient
                    .get()
                    .uri(builder -> builder.path(ADRESSER_VEG_URL).query(query).build())
                    .header("antall", "1")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(VegadresseDTO[].class);

        } catch (WebClientResponseException e) {
            log.error("Feil ved henting av adresser: {}.", e.getResponseBodyAsString());
            return null;
        }
    }
}
