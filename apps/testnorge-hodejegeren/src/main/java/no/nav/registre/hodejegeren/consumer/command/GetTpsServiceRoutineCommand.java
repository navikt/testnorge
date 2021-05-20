package no.nav.registre.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import no.nav.registre.hodejegeren.consumer.dto.ServiceRoutineDTO;

@Slf4j
@RequiredArgsConstructor
public class GetTpsServiceRoutineCommand implements Callable<Flux<ServiceRoutineDTO>> {
    private final WebClient webClient;
    private final String routineName;
    private final String aksjonsKode;
    private final String miljoe;
    private final String fnr;

    @Override
    public Flux<ServiceRoutineDTO> call() {

        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/v1/serviceroutine/{routineName}")
                            .queryParam("aksjonsKode", aksjonsKode)
                            .queryParam("environment", miljoe)
                            .queryParam("fnr", fnr)
                            .build(routineName)
                    )
                    .retrieve()
                    .bodyToFlux(ServiceRoutineDTO.class);
        } catch (WebClientResponseException e) {
            log.error("feil ",e);
            throw e;
        }

    }
}
