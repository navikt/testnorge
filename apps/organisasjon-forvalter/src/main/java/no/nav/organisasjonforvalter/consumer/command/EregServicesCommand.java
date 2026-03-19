package no.nav.organisasjonforvalter.consumer.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.dto.responses.ereg.EregServicesResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class EregServicesCommand implements Callable<Mono<EregServicesResponse>> {

    private static final String STATUS_URL = "/v2/organisasjon/{orgnummer}";

    private final WebClient webClient;
    private final String orgnummer;
    private final String miljoe;

    @Override
    public Mono<EregServicesResponse> call() {
        log.info("Henter organisasjon {} fra miljø {} ", orgnummer, miljoe);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(STATUS_URL)
                        .queryParam("inkluderHistorikk", false)
                        .queryParam("inkluderHierarki", true)
                        .build(orgnummer))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> EregServicesResponse.builder()
                        .miljoe(miljoe)
                        .organisasjon(jsonNode)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> Mono.just(EregServicesResponse.builder()
                        .error(WebClientError.describe(throwable).getMessage())
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }
}