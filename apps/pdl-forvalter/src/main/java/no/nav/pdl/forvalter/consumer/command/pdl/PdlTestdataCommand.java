package no.nav.pdl.forvalter.consumer.command.pdl;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlStatus.OK;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.pdl.forvalter.consumer.command.pdl.TestdataCommand;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;

@Slf4j
@RequiredArgsConstructor
public class PdlTestdataCommand extends TestdataCommand {
    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final Object body;
    private final String token;
    private final Integer id;

    @Override
    public Mono<PdlOrdreResponse.Hendelse> call() {

        if (url.contains("/opprettperson")) {
            webClient
                    .post()
                    .uri(builder -> builder.path(url)
                            .queryParam("kilde", "Dolly")
                            .build())
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
                    .doOnError(error -> Mono.just(errorHandling(error, id)));

        }

        return webClient
                .post()
                .uri(url)
                .body(BodyInserters.fromValue(body))
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
                .doOnError(error -> Mono.just(errorHandling(error, id)));
    }
}
