package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.testnav.libs.data.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

@Slf4j
@RequiredArgsConstructor
public class PdlDeleteHendelseIdCommandPdl extends PdlTestdataCommand {

    private static final String HENDELSEID = "hendelseId";
    private static final String INFO_STATUS = "Finner ikke forespurt ident i pdl-api";

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final String hendelseId;
    private final String token;

    @Override
    public Flux<OrdreResponseDTO.HendelseDTO> call() {

        return webClient
                .delete()
                .uri(builder -> builder.path(url)
                        .queryParam("kilde", "Dolly")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .header(HENDELSEID, hendelseId)
                .retrieve()
                .bodyToFlux(PdlBestillingResponse.class)
                .flatMap(response -> Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                        .status(PdlStatus.OK)
                        .deletedOpplysninger(response.getDeletedOpplysninger())
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebServerException.class, error -> log.error(error.getMessage(), error))
                .onErrorResume(error ->
                        Mono.just(OrdreResponseDTO.HendelseDTO.builder()
                                .status(getMessage(error).contains(INFO_STATUS) ? PdlStatus.OK : PdlStatus.FEIL)
                                .error(getMessage(error).contains(INFO_STATUS) ? null : getMessage(error))
                                .build())
                );
    }
}