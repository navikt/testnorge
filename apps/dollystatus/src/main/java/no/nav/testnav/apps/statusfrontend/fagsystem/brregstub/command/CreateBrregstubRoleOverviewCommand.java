package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.BrregstubRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateBrregstubRoleOverviewCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final BrregstubRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/brregstub/api/v2/rolleoversikt")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BrregstubRequest.class)
                .filter(request::equals)
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "Brregstub-opprettingen returnerte ugyldig respons.")))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }
}
