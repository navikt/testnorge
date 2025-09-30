package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class NySykemeldingDeleteCommand implements Callable<Mono<Void>> {

    private static final String TSM_SYKEMELDING_URL = "/tsm/api/sykmelding/ident";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<Void> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(TSM_SYKEMELDING_URL).build())
                .headers(WebClientHeader.bearer(token))
                .header("X-ident", ident)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(throwable -> !(throwable instanceof WebClientResponseException.NotFound), WebClientError.logTo(log))
                .doOnSuccess(response -> log.info("Slettet sykemeldinger i tsm for ident: {}", ident));
    }
}