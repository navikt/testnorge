package no.nav.dolly.bestilling.dokarkiv.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class DokarkivPostCommand implements Callable<Mono<DokarkivResponse>> {

    private final WebClient webClient;
    private final String environment;
    private final DokarkivRequest dokarkivRequest;
    private final String token;


    @Override
    public Mono<DokarkivResponse> call() {

        return webClient.post()
                .uri(builder ->
                        builder.path("/api/{miljo}/v1/journalpost")
                                .queryParam("forsoekFerdigstill", isTrue(dokarkivRequest.getFerdigstill()))
                                .build(environment))
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(dokarkivRequest)
                .retrieve()
                .bodyToMono(DokarkivResponse.class)
                .map(response -> {
                    response.setMiljoe(environment);
                    return response;
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(DokarkivResponse.builder()
                        .feilmelding(WebClientFilter.getMessage(error))
                        .miljoe(environment)
                        .build()));
    }
}
