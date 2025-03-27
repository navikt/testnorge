package no.nav.dolly.bestilling.brregstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.brregstub.domain.RolleoversiktTo;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class BrregPostCommand implements Callable<Mono<RolleoversiktTo>> {

    private static final String ROLLEOVERSIKT_URL = "/api/v2/rolleoversikt";

    private final WebClient webClient;
    private final RolleoversiktTo rolleoversiktTo;
    private final String token;

    @Override
    public Mono<RolleoversiktTo> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(ROLLEOVERSIKT_URL).build())
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .bodyValue(rolleoversiktTo)
                .retrieve()
                .bodyToMono(RolleoversiktTo.class)
                .onErrorResume(throwable -> RolleoversiktTo.of(WebClientError.describe(throwable)))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
