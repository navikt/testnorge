package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.synt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class HentVedtakshistorikkCommand implements Callable<Mono<List<Vedtakshistorikk>>> {

    private final WebClient webClient;
    private final List<String> oppstartsdatoer;
    private final String token;

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Vedtakshistorikk>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<Vedtakshistorikk>> call() {
        log.info("Henter vedtakshistorikk.");
        return webClient
                .post()
                .uri(builder -> builder.path("/api/v1/vedtakshistorikk").build())
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromPublisher(Mono.just(oppstartsdatoer), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
