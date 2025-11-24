package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.testnav.libs.data.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

@Slf4j
@RequiredArgsConstructor
public class PdlDeleteCommandPdl extends PdlTestdataCommand {

    private static final String INFO_STATUS = "Finner ikke forespurt ident i pdl-api";

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final String token;

    @Override
    public Flux<OrdreResponseDTO.HendelseDTO> call() {
        return webClient
                .delete()
                .uri(builder -> builder.path(url)
                        .queryParam("kilde", "Dolly")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .bodyToFlux(PdlBestillingResponse.class)
                .timeout(TIMEOUT)
                .flatMap(response -> Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                        .status(PdlStatus.OK)
                        .deletedOpplysninger(response.getDeletedOpplysninger())
                        .build()))
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> {
                    var message = WebClientError.describe(throwable).getMessage();
                    return Mono.just(OrdreResponseDTO.HendelseDTO
                            .builder()
                            .status(message.contains(INFO_STATUS) ? PdlStatus.OK : PdlStatus.FEIL)
                            .error(message.contains(INFO_STATUS) ? null : message)
                            .build());
                });
    }

}