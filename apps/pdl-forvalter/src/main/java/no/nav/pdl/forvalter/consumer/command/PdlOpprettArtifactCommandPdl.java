package no.nav.pdl.forvalter.consumer.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

@RequiredArgsConstructor
@Slf4j
public class PdlOpprettArtifactCommandPdl extends PdlTestdataCommand {

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final Object body;
    private final String token;
    private final Integer id;

    @Override
    public Flux<OrdreResponseDTO.HendelseDTO> call() {
        log.info("Sending PDL artifact to {} for ident {}: {}", url, ident, Json.pretty(body));
        return webClient
                .post()
                .uri(builder -> builder.path(url).build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .bodyToFlux(PdlBestillingResponse.class)
                .timeout(TIMEOUT)
                .flatMap(response -> Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                        .id(id)
                        .status(PdlStatus.OK)
                        .hendelseId(response.getHendelseId())
                        .build()))
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> Mono.just(errorHandling(error, id)));
    }

}
