package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.inntektstub;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class DeleteInntekterCommand implements Callable<Mono<Void>> {

    private final List<String> identer;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<Void> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v2/personer")
                        .queryParam("norske-identer", identer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(WebClientFilter::logErrorMessage);
    }

}
