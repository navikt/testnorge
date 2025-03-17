package no.nav.testnav.altinn3tilgangservice.consumer.brreg.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.BrregResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetBrregEnheterCommand implements Callable<Mono<BrregResponseDTO>> {

    private static final String BRREG_ENHETER_URL = "/enhetsregisteret/api/enheter";

    private final WebClient webClient;
    private final String organisasjonsnummer;

    @Override
    public Mono<BrregResponseDTO> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(BRREG_ENHETER_URL)
                        .queryParam("organisasjonsnummer", organisasjonsnummer)
                        .build()
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(BrregResponseDTO.class)
                .doOnError(WebClientError.logTo(log));
    }
}
