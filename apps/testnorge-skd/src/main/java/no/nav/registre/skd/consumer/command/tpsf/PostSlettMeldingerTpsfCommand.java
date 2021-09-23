package no.nav.registre.skd.consumer.command.tpsf;

import java.util.concurrent.Callable;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.consumer.requests.SlettSkdmeldingerRequest;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class PostSlettMeldingerTpsfCommand implements Callable<HttpStatus> {

    private final SlettSkdmeldingerRequest slettSkdmeldingerRequest;
    private final WebClient webClient;

    @Override
    public HttpStatus call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/deletemeldinger").build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(slettSkdmeldingerRequest), SlettSkdmeldingerRequest.class))
                .exchange()
                .map(ClientResponse::statusCode)
                .block();
    }
}
