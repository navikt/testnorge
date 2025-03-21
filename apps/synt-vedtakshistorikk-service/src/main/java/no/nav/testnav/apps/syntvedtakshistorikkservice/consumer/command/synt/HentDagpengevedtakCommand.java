package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.synt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengevedtakDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Dagpengerettighet;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class HentDagpengevedtakCommand implements Callable<Mono<List<DagpengevedtakDTO>>> {

    private final WebClient webClient;
    private final List<String> oppstartsdatoer;
    private final Dagpengerettighet rettighet;
    private final String token;

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<DagpengevedtakDTO>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<DagpengevedtakDTO>> call() {
        log.info("Henter syntetisk dagpengevedtak.");
        return webClient
                .post()
                .uri(builder -> builder.path("/api/v1/vedtak/{rettighet}").build(rettighet))
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromPublisher(Mono.just(oppstartsdatoer), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}

