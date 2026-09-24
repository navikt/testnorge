package no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetInntektstubIncomeCommand implements Callable<Mono<InntektstubResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final InntektstubRequest expectedRequest;
    private final Duration timeout;

    @Override
    public Mono<InntektstubResourceStatus> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/inntektstub/api/v2/inntektsinformasjon")
                        .queryParam("norske-identer", expectedRequest.norskIdent())
                        .queryParam("historikk", true)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody().thenReturn(
                                InntektstubResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(body -> InntektstubResourceStatus.from(
                                        body,
                                        expectedRequest))
                                .defaultIfEmpty(InntektstubResourceStatus.emptyStatus());
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<InntektstubResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
