package no.nav.skattekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.skattekortservice.dto.v2.HentSkattekortRequest;
import no.nav.skattekortservice.dto.v2.SkattekortDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SokosGetCommand implements Callable<Flux<SkattekortDTO>> {

    private static final String SOKOS_URL = "/api/v1/person/hent-skattekort";

    private final WebClient webClient;
    private final HentSkattekortRequest request;
    private final String token;

    @Override
    public Flux<SkattekortDTO> call() {
        log.info("Henter skattekort fra Sokos for fnr: {}, inntektsår: {}",
                request.getFnr(), request.getInntektsaar());

        return webClient
                .post()
                .uri(SOKOS_URL)
                .headers(WebClientHeader.bearer(token))
                .header("korrelasjonsid", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToFlux(SkattekortDTO.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}