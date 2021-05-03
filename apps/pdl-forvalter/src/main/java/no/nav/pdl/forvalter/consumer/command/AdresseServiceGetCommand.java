package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.AdresseResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class AdresseServiceGetCommand implements Callable<AdresseResponse> {

    private final WebClient webClient;
    private final String url;
    private final Integer antall;
    private final String token;

    @Override
    public AdresseResponse call() {

        try {
            return webClient
                    .get()
                    .uri(builder -> builder.path(url).build())
                    .header("antall", nonNull(antall) ? antall.toString() : "1")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(AdresseResponse.class)
                    .block();

        } catch (WebClientResponseException e) {
            log.error("Feil ved henting av adresser: {}.", e.getResponseBodyAsString());
            return null;
        }
    }
}
