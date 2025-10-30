package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.inntektstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class DeleteInntekterCommand implements Callable<Mono<Void>> {

    private final List<String> identer;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<Void> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/inntektstub/api/v2/personer")
                        .queryParam("norske-identer", identer)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(WebClientError.logTo(log));
    }

}
