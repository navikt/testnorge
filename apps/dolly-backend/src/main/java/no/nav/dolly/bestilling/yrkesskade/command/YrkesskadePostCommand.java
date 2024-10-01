package no.nav.dolly.bestilling.yrkesskade.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class YrkesskadePostCommand implements Callable<Flux<ResponseEntity<String>>> {

    private static final String YRKESSKADE_URL = "/api/v1/yrkesskader";

    private final WebClient webClient;
    private final YrkesskadeRequest yrkesskadeRequest;
    private final String token;

    @Override
    public Flux<ResponseEntity<String>> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(YRKESSKADE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(yrkesskadeRequest)
                .retrieve()
                .bodyToFlux(ResponseEntity.class)
                .map(response -> (new ResponseEntity<>(response.toString(), response.getStatusCode())))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(
                        WebClientFilter.getMessage(throwable),
                        WebClientFilter.getStatus(throwable))))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}