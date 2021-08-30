package no.nav.registre.skd.consumer.command.identpool;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
import reactor.core.publisher.Mono;

@Slf4j
public class PostHentIdenterCommand implements Callable<List<String>> {

    private final HentIdenterRequest hentIdenterRequest;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    public PostHentIdenterCommand(HentIdenterRequest hentIdenterRequest, WebClient webClient){
        this.hentIdenterRequest = hentIdenterRequest;
        this.webClient = webClient;
    }

    @Override
    public List<String> call(){
        try {
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
        } catch (Exception e) {
            log.error("Klarte ikke å hente identer.", e);
            return Collections.emptyList();
        }
    }

}
