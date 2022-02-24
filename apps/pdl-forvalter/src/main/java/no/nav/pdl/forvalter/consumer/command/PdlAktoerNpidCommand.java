package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class PdlAktoerNpidCommand extends PdlTestdataCommand {

    private static final String PDL_AKTOER_ADMIN_PREFIX = "/pdl-npid";
    private static final String PDL_PERSON_AKTOER_URL = PDL_AKTOER_ADMIN_PREFIX + "/api/npid";

    private final WebClient webClient;
    private final String npid;
    private final String token;

    public Mono<OrdreResponseDTO.HendelseDTO> call() {

        return webClient
                .post()
                .uri(PDL_PERSON_AKTOER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(npid))
                .exchangeToMono(response ->
                        Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                                .status(PdlStatus.OK)
                                .build()))
                .doOnError(WebServerException.class, error -> log.error(error.getMessage(), error))
                .onErrorResume(error -> Mono.just(errorHandling(error, null)));
    }
}