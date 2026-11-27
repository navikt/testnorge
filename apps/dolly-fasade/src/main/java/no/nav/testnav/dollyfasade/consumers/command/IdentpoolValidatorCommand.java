package no.nav.testnav.dollyfasade.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.identpool.v2.ValideringFlereRequestDTO;
import no.nav.testnav.libs.dto.identpool.v2.ValideringResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class IdentpoolValidatorCommand implements Callable<Flux<ValideringResponseDTO>> {

    private static final String VALIDATOR_URL = "/api/v2/ident/validerflere";

    private final WebClient webClient;
    private final ValideringFlereRequestDTO request;
    private final String token;

    @Override
    public Flux<ValideringResponseDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder.path(VALIDATOR_URL)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(ValideringResponseDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }
}