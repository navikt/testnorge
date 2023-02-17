package no.nav.dolly.bestilling.brregstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class BrregPostCommand implements Callable<Mono<RolleoversiktTo>> {

    private static final String ROLLEOVERSIKT_URL = "/api/v2/rolleoversikt";

    private final WebClient webClient;
    private final RolleoversiktTo rolleoversiktTo;
    private final String token;

    @Override
    public Mono<RolleoversiktTo> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(ROLLEOVERSIKT_URL).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(rolleoversiktTo)
                .retrieve()
                .bodyToMono(RolleoversiktTo.class)
                .onErrorResume(error -> Mono.just(RolleoversiktTo.builder()
                        .error(WebClientFilter.getMessage(error))
                        .build()))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
