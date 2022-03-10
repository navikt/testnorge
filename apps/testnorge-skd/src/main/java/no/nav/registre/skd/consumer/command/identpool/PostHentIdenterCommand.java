package no.nav.registre.skd.consumer.command.identpool;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
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
public class PostHentIdenterCommand implements Callable<List<String>> {

    private final HentIdenterRequest hentIdenterRequest;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<String> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/identifikator")
                                .queryParam("finnNaermesteLedigeDato", false)
                                .build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(hentIdenterRequest), HentIdenterRequest.class))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

}
