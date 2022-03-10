package no.nav.registre.skd.consumer.command.tpsf;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class PostSendSkdMeldingerTpsCommand implements Callable<SkdMeldingerTilTpsRespons> {

    private final Long gruppeId;
    private final SendToTpsRequest sendToTpsRequest;
    private final WebClient webClient;

    @Override
    public SkdMeldingerTilTpsRespons call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/send/{gruppeId}")
                                .build(gruppeId)
                )
                .body(BodyInserters.fromPublisher(Mono.just(sendToTpsRequest), SendToTpsRequest.class))
                .retrieve()
                .bodyToMono(SkdMeldingerTilTpsRespons.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
