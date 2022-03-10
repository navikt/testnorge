package no.nav.registre.skd.consumer.command.tpsf;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class PostSaveSkdEndringsmeldingerTpsfCommand implements Callable<List<Long>> {

    private final Long gruppeId;
    private final List<RsMeldingstype> skdmeldinger;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<RsMeldingstype>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<Long> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/save/{gruppeId}")
                                .build(gruppeId)
                )
                .body(BodyInserters.fromPublisher(Mono.just(skdmeldinger), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
