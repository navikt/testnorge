package no.nav.registre.skd.consumer.command.tpsf;

import java.util.concurrent.Callable;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import reactor.core.publisher.Mono;

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
                .block();
    }
}
