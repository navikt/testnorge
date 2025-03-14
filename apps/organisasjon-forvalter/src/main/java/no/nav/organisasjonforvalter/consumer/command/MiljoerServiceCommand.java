package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class MiljoerServiceCommand implements Callable<Mono<String[]>> {

    private static final String MILJOER_URL = "/api/v1/miljoer";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<String[]> call() {
        return webClient
                .get()
                .uri(MILJOER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(throwable -> Mono.empty());
    }

}
