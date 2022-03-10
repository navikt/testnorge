package no.nav.registre.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.consumer.dto.ServiceRoutineDTO;
import no.nav.registre.hodejegeren.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetTpsServiceRoutineCommand implements Callable<ServiceRoutineDTO> {
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
                        .path("/v1/serviceroutine/{routineName}")
                        .queryParam("aksjonsKode", aksjonsKode)
                        .queryParam("environment", miljoe)
                        .queryParam("fnr", fnr)
                        .build(routineName)
                )
                .retrieve()
                .bodyToMono(ServiceRoutineDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

    }
}
