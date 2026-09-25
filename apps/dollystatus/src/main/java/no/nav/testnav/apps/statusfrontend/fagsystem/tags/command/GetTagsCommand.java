package no.nav.testnav.apps.statusfrontend.fagsystem.tags.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTagsCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.get()
                .uri("/pdl-testdata/api/v1/bestilling/tags")
                .headers(headers -> headers.setBearerAuth(token))
                .header("Nav-Personident", ident)
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
