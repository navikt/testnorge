package no.nav.testnav.apps.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.hodejegeren.consumer.dto.ServiceRoutineDTO;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTpsServiceRoutineV2Command implements Callable<ServiceRoutineDTO> {
    private final WebClient webClient;
    private final String routineName;
    private final String aksjonsKode;
    private final String miljoe;
    private final String fnr;

    @Override
    public ServiceRoutineDTO call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/serviceroutine/{routineName}")
                        .queryParam("aksjonsKode", aksjonsKode)
                        .queryParam("environment", miljoe)
                        .queryParam("fnr", fnr)
                        .build(routineName)
                )
                .retrieve()
                .bodyToMono(ServiceRoutineDTO.class)
                .block();

    }
}
