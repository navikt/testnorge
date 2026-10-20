package no.nav.testnav.apps.statusfrontend.fagsystem.krr.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateKrrContactInformationCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final KrrRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/krrstub/api/v2/kontaktinformasjon")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(token))
                .header("Nav-Consumer-Id", "Dolly")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }
}
