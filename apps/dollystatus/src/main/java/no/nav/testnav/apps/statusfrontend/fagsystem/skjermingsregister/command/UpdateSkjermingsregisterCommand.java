package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class UpdateSkjermingsregisterCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final SkjermingsregisterRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.put()
                .uri("/skjermingsregister/api/v1/skjerming/dolly")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(request)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()
                            || response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody();
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<Void>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
