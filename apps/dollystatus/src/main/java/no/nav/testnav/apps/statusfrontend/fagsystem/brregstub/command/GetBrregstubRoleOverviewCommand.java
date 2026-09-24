package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.BrregstubRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.BrregstubResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetBrregstubRoleOverviewCommand implements Callable<Mono<BrregstubResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final BrregstubRequest expectedRequest;
    private final Duration timeout;

    @Override
    public Mono<BrregstubResourceStatus> call() {
        return webClient.get()
                .uri("/brregstub/api/v2/rolleoversikt")
                .headers(headers -> headers.setBearerAuth(token))
                .header("Nav-Personident", ident)
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody().thenReturn(BrregstubResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(BrregstubRequest.class)
                                .map(actual -> new BrregstubResourceStatus(
                                        false,
                                        expectedRequest.equals(actual)))
                                .switchIfEmpty(Mono.error(new IllegalStateException(
                                        "Brregstub-oppslaget returnerte tom respons.")));
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<BrregstubResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
