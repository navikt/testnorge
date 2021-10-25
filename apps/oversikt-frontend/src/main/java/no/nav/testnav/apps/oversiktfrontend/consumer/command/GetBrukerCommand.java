package no.nav.testnav.apps.oversiktfrontend.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.oversiktfrontend.consumer.dto.BrukerDTO;

@RequiredArgsConstructor
public class GetBrukerCommand implements Callable<Mono<BrukerDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String orgnummer;

    @Override
    public Mono<BrukerDTO> call() {
        return webClient
                .get()
                .uri(builder ->builder.path("/api/v1/brukere").queryParam("organisasjonsnummer", orgnummer).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(BrukerDTO.class)
                .next();
    }
}
