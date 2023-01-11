package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.instdata.domain.DeleteResponse;
import no.nav.dolly.util.TokenXUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class InstdataDeleteCommand implements Callable<Mono<DeleteResponse>> {

    private static final String INSTDATA_URL = "/api/v1/institusjonsopphold/person";

    private static final String ENVIRONMENTS = "environments";
    private static final String INST_IDENT = "norskident";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final String token;

    @Override
    public Mono<DeleteResponse> call() {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTDATA_URL)
                        .queryParam(ENVIRONMENTS, miljoer)
                        .build())
                .header(INST_IDENT, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, TokenXUtil.getUserJwt())
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> DeleteResponse.builder()
                        .ident(ident)
                        .status(resultat.getStatusCode())
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(DeleteResponse.builder()
                        .ident(ident)
                        .status(WebClientFilter.getStatus(error))
                        .error(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
