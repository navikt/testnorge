package no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateInntektstubIncomeCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final InntektstubRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/inntektstub/api/v2/inntektsinformasjon")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(List.of(request))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(body -> InntektstubResourceStatus.from(body, request))
                .filter(InntektstubResourceStatus::expectedDataPresent)
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "Inntektstub-opprettingen returnerte ugyldig respons.")))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }
}
