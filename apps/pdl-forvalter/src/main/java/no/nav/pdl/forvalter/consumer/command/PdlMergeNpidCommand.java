package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public class PdlMergeNpidCommand extends PdlTestdataCommand {

    private static final String NPID = "npid";
    private static final String OTHER_IDENT = "otherIdent";

    private final WebClient webClient;
    private final String url;
    private final String npid;
    private final String otherIdent;
    private final String token;

    @Override
    public Flux<OrdreResponseDTO.HendelseDTO> call() {

        return webClient
                .post()
                .uri(builder -> builder.path(url)
                        .queryParam(NPID, npid)
                        .queryParam(OTHER_IDENT, otherIdent)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response ->
                        Flux.just(OrdreResponseDTO.HendelseDTO.builder()
                                .status(PdlStatus.OK)
                                .build()))
                .doOnError(WebServerException.class, error -> log.error(error.getMessage(), error))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error -> Mono.just(errorHandling(error, null)));
    }
}