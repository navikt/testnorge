package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GenererNavnServiceCommand implements Callable<NavnDTO[]> {

    private final WebClient webClient;
    private final String url;
    private final Integer antall;
    private final String token;

    @Override
    public NavnDTO[] call() {

        try {
            return webClient
                    .get()
                    .uri(builder -> builder.path(url).queryParam("antall", antall).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(NavnDTO[].class)
                    .block();

        } catch (
                WebClientResponseException e) {
            log.error("Feil ved henting av navn fra navneservice {}.", e.getResponseBodyAsString(), e);
            throw e;
        }
    }
}
