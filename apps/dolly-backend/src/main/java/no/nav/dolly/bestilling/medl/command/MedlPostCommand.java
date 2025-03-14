package no.nav.dolly.bestilling.medl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.medl.dto.MedlPostResponse;
import no.nav.dolly.domain.resultset.medl.MedlData;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class MedlPostCommand implements Callable<Mono<MedlPostResponse>> {

    private static final String MEDL_URL = "/rest/v1/medlemskapsperiode";

    private final WebClient webClient;
    private final MedlData medlData;
    private final String token;

    @Override
    public Mono<MedlPostResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(MEDL_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(medlData)
                .retrieve()
                .toBodilessEntity()
                .map(response -> {
                    log.info("Mottok response fra Medl post: \n{}", response);
                    return MedlPostResponse.builder()
                            .status(HttpStatus.valueOf(response.getStatusCode().value()))
                            .build();
                })
                .doOnError(throwable -> log.error(throwable.getLocalizedMessage()))
                .onErrorResume(error -> Mono.just(MedlPostResponse.builder()
                        .status(WebClientFilter.getStatus(error))
                        .melding(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
