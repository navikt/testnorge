package no.nav.testnav.apps.importfratpsfservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import no.nav.testnav.apps.importfratpsfservice.utils.ErrorhandlerUtils;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class TpsfGetSkdMeldingerCommand implements Callable<Flux<SkdEndringsmelding>> {

    private static final String TPSF_SKD_GRUPPE_URL = "/api/v1/endringsmelding/skd/gruppe/meldinger/{gruppeId}/{pageNumber}";
    private static final String IMPORT_FRA_TPSF = "Les SKD-melding (GET) fra TPS-forvalteren: ";

    private final WebClient webClient;
    private final Long gruppeId;
    private final Long pageNumber;
    private final String token;

    protected static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString() :
                error.getMessage();
    }

    @Override
    public Flux<SkdEndringsmelding> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(TPSF_SKD_GRUPPE_URL).build(gruppeId, pageNumber))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(SkdEndringsmelding.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> ErrorhandlerUtils.handleError(throwable, IMPORT_FRA_TPSF));
    }
}
