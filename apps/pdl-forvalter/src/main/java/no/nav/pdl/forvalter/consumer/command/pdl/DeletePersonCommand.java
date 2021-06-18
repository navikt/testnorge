package no.nav.pdl.forvalter.consumer.command.pdl;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlStatus.OK;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;

@Slf4j
@RequiredArgsConstructor
public class DeletePersonCommand extends TestdataCommand {
    private final WebClient webClient;
    private final String ident;
    private final String token;
    private final Integer id;

    @Override
    public Mono<PdlOrdreResponse.Hendelse> call() {
        return webClient
                .delete()
                .uri("/pdl-testdata/api/v1/personident")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .exchange()
                .flatMap(response -> response
                        .bodyToMono(PdlBestillingResponse.class)
                        .map(value -> PdlOrdreResponse.Hendelse.builder()
                                .id(id)
                                .status(OK)
                                .hendelseId(value.getHendelseId())
                                .deletedOpplysninger(value.getDeletedOpplysninger())
                                .build())
                )
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        if (!((WebClientResponseException) error).getResponseBodyAsString().contains("Finner ikke forespurt ident i pdl-api")) {
                            log.error("Sletting av person feilet mot pdl \n{}", ((WebClientResponseException) error).getResponseBodyAsString(), error);
                        }
                    } else {
                        log.error("Fetting av person feilet mot pdl.", error);
                    }
                });
    }
}
