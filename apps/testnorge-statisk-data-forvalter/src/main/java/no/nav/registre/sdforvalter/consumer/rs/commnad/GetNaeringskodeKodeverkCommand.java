package no.nav.registre.sdforvalter.consumer.rs.commnad;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.sdforvalter.consumer.rs.dto.KodeverkDTO;

@RequiredArgsConstructor
public class GetNaeringskodeKodeverkCommand implements Callable<KodeverkDTO> {
    private final WebClient webClient;
    @Override
    public KodeverkDTO call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/kodeverksoversikt/kodeverk/Næringskoder/koder")
                        .queryParam("spraak", "no")
                        .queryParam("periode", "GYLDIG")
                        .build()
                )
                .retrieve()
                .bodyToMono(KodeverkDTO.class)
                .block();
    }
}
