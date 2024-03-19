package no.nav.dolly.bestilling.aareg.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.util.TokenXUtil;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.RequestTimeout.REQUEST_DURATION;

@RequiredArgsConstructor
public class AmeldingPutCommand implements Callable<Mono<ResponseEntity<String>>> {

    private static final String AMELDING_URL = "/api/v1/amelding";
    private static final String MILJOE = "miljo";

    private final WebClient webClient;
    private final AMeldingDTO amelding;
    private final String miljo;
    private final String token;

    @Override
    public Mono<ResponseEntity<String>> call() {

        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path(AMELDING_URL)
                        .build())
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(REQUEST_DURATION));
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, TokenXUtil.getUserJwt())
                .header(MILJOE, miljo)
                .bodyValue(amelding)
                .retrieve()
                .toBodilessEntity()
                .map(respons -> ResponseEntity.status(respons.getStatusCode()).body("OK"))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(ResponseEntity.status(WebClientFilter.getStatus(error))
                                .body(WebClientFilter.getMessage(error))))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
