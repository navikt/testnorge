package no.nav.dolly.budpro.navn;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.texas.Texas;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Copied and modified from {@code no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand}.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class GenererNavnCommand implements Callable<Flux<NavnDTO>> {

    private final Texas texas;
    private Long seed;
    private final Integer antall;

    @Override
    public Flux<NavnDTO> call() {
        return texas
                .webClient("generer-navn-service")
                .get()
                .uri(builder -> builder
                        .path("/api/v1/navn")
                        .queryParamIfPresent("seed", Optional.ofNullable(seed))
                        .queryParam("antall", antall)
                        .build())
                .headers(texas.bearer("generer-navn-service"))
                .retrieve()
                .bodyToFlux(NavnDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnError(e -> log.error("Failed to get names from generer-navn-service", e));
    }

}
