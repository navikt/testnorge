package no.nav.registre.skd.consumer.command.identpool;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
import reactor.core.publisher.Mono;

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
                .block();
    }

}
