package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.testnav.libs.data.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PdlDataOpprettingCommand implements Callable<Flux<PdlResponse>> {

    private static final String PDL_FORVALTER_PERSONER_URL = "/api/v1/personer";

    private final WebClient webClient;
    private final BestillingRequestDTO body;
    private final String token;

    public Flux<PdlResponse> call() {

        return webClient
                .post()
                .uri(PDL_FORVALTER_PERSONER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToFlux(String.class)
                .map(resultat -> PdlResponse.builder()
                        .ident(resultat)
                        .status(HttpStatus.OK)
                        .build())
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on POST"))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Flux.just(PdlResponse.builder()
                                .status(WebClientFilter.getStatus(error))
                                .feilmelding(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
