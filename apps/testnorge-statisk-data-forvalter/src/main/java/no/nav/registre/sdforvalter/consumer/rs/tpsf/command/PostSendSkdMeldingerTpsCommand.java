package no.nav.registre.sdforvalter.consumer.rs.tpsf.command;

import lombok.AllArgsConstructor;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.request.SendToTpsRequest;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.sdforvalter.util.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class PostSendSkdMeldingerTpsCommand implements Callable<Mono<SkdMeldingerTilTpsRespons>> {

    private final Long gruppeId;
    private final SendToTpsRequest sendToTpsRequest;
    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<SkdMeldingerTilTpsRespons> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/endringsmelding/skd/send/{gruppeId}")
                                .build(gruppeId)
                )
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(sendToTpsRequest), SendToTpsRequest.class))
                .retrieve()
                .bodyToMono(SkdMeldingerTilTpsRespons.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
