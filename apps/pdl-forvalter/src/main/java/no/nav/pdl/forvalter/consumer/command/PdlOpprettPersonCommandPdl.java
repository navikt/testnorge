package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.HistoriskIdent;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PdlStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

@Slf4j
@RequiredArgsConstructor
public class PdlOpprettPersonCommandPdl extends PdlTestdataCommand {

    private static final String IDENTHISTORIKK = "historiskePersonidenter";

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final HistoriskIdent historiskeIdenter;
    private final String token;

    @Override
    public Mono<OrdreResponseDTO.HendelseDTO> call() {

        return webClient
                .post()
                .uri(builder -> builder.path(url)
                        .queryParam("kilde", "Dolly")
                        .queryParam(IDENTHISTORIKK, historiskeIdenter.getIdenter())
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .exchange()
                .flatMap(response -> response.bodyToMono(PdlBestillingResponse.class)
                        .map((value -> OrdreResponseDTO.HendelseDTO.builder()
                                .status(PdlStatus.OK)
                                .build())))
                .doOnError(error -> Mono.just(errorHandling(error, null)));
    }
}
