package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GetLevendeIdenterCommand implements Callable<Flux<String>> {
    private final WebClient webClient;
    private final String miljo;
    private final int max;
    private final String token;
    @Override
    public Flux<String> call() {
        log.info("Henter {} identer i {}...", max, miljo);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v2/levende-identer/100000883")
                        .queryParam("miljoe", miljo)
                        .queryParam("max", max)
                        .queryParam("minimumAlder", 18)
                        .build()
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
