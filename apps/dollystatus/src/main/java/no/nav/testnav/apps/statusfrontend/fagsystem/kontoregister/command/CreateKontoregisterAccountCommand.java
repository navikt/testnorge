package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateKontoregisterAccountCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final OppdaterKontoRequestDTO request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/kontoregister/api/system/v1/oppdater-konto")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }
}
