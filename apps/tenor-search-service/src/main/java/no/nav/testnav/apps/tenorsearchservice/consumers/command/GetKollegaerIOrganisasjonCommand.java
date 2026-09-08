package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.BrukereDTO;
import no.nav.testnav.apps.tenorsearchservice.exception.BrukerServiceUnavailableException;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetKollegaerIOrganisasjonCommand implements Callable<Mono<BrukereDTO>> {

    private static final String TILGANG_URL = "/api/v1/tilgang";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final WebClient webClient;
    private final String brukerId;
    private final String token;

    @Override
    public Mono<BrukereDTO> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TILGANG_URL)
                        .queryParam("brukerId", brukerId)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(BrukereDTO.class)
                .timeout(TIMEOUT)
                .retryWhen(WebClientError.is5xxException())
                .onErrorMap(error -> new BrukerServiceUnavailableException(
                        "Kunne ikke hente tilgjengelige malbrukere.", error));
    }
}
