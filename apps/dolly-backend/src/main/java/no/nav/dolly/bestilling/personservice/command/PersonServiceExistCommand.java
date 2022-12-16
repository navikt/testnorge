package no.nav.dolly.bestilling.personservice.command;

import lombok.RequiredArgsConstructor;
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
public class PersonServiceExistCommand implements Callable<Mono<Boolean>> {

   private final WebClient webClient;
   private final String ident;
   private final String token;

    private static final String PERSON_URL = "/api/v1/personer/{ident}/exists";

    @Override
    public Mono<Boolean> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(PERSON_URL)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(Boolean.FALSE))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}