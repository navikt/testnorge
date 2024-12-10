package no.nav.registre.testnorge.sykemelding.consumer.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.sykemelding.dto.ReceivedSykemeldingDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.ValidationResultDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SyfosmreglerPostValidateCommand implements Callable<Mono<ValidationResultDTO>> {

    private static final String SYFOSMREGLER_VALIDATE_URL = "/v1/rules/validate";

    private final WebClient webClient;
    private final ReceivedSykemeldingDTO receivedSykemelding;
    private final String accessToken;

    @Override
    public Mono<ValidationResultDTO> call() {

        log.info("Sender til syfosmregler {}", Json.pretty(receivedSykemelding));

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(SYFOSMREGLER_VALIDATE_URL).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue(receivedSykemelding)
                .retrieve()
                .bodyToMono(ValidationResultDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(ValidationResultDTO.builder()
                        .httpStatus(WebClientFilter.getStatus(error))
                        .message(WebClientFilter.getMessage(error))
                        .build()));
    }
}
