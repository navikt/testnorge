package no.nav.registre.skd.consumer.command.tpsf;

import lombok.AllArgsConstructor;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class PostHentMeldingsIdsCommand implements Callable<List<Long>> {

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final Long avspillergruppeId;
    private final List<String> identer;
    private final WebClient webClient;

    @Override
    public List<Long> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/meldinger/{avspillergruppeId}")
                                .build(avspillergruppeId)
                )
                .body(BodyInserters.fromPublisher(Mono.just(identer), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
