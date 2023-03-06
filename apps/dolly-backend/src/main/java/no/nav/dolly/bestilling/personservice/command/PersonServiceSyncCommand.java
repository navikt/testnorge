package no.nav.dolly.bestilling.personservice.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class PersonServiceSyncCommand implements Callable<Flux<Boolean>> {

    private static final String PERSON_URL = "/api/v1/personer/{ident}/sync";
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<Boolean> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(PERSON_URL)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(Boolean.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(Mono::error)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}