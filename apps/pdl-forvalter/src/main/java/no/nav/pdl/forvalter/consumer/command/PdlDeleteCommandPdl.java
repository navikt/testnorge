package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public class PdlDeleteCommandPdl extends PdlTestdataCommand {

    private static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    private static final String TEMA = "Tema";

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final String token;

    @Override
    public Mono<OrdreResponseDTO.HendelseDTO> call() {

        return webClient
                .delete()
                .uri(builder -> builder.path(url).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .exchange()
                .flatMap(response -> response.bodyToMono(PdlBestillingResponse.class)
                        .map(value ->
                                OrdreResponseDTO.HendelseDTO.builder()
                                .status(isBlank(value.getFeilmelding()) ? PdlStatus.OK : PdlStatus.FEIL)
                                .deletedOpplysninger(value.getDeletedOpplysninger())
                                .error(value.getFeilmelding())
                                .build()))
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        if (!((WebClientResponseException) error)
                                .getResponseBodyAsString().contains("Finner ikke forespurt ident i pdl-api")) {
                            log.error("Sletting av person feilet mot pdl: {}",
                                    ((WebClientResponseException) error).getResponseBodyAsString(), error);
                        }
                    } else {
                        log.error("Sletting av person feilet mot pdl ", error);
                    }
                });
    }
}