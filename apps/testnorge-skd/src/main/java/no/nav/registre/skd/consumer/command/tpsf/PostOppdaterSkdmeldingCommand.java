package no.nav.registre.skd.consumer.command.tpsf;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class PostOppdaterSkdmeldingCommand implements Callable<HttpStatus> {

    private final List<RsMeldingstype> meldinger;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<RsMeldingstype>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public HttpStatus call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/updatemeldinger").build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(meldinger), REQUEST_TYPE))
                .exchange()
                .map(ClientResponse::statusCode)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

}
