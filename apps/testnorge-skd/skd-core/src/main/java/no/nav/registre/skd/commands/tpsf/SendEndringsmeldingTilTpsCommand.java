package no.nav.registre.skd.commands.tpsf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SendEndringsmeldingTilTpsCommand implements Callable<SkdMeldingerTilTpsRespons> {
    private final WebClient webClient;
    private final Long avspillergruppeId;
    private final SendToTpsRequest sendToTpsRequest;

    @Override
    public SkdMeldingerTilTpsRespons call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/endringsmelding/skd/send/")
                        .pathSegment(avspillergruppeId.toString())
                        .build())
                .body(BodyInserters.fromValue(sendToTpsRequest))
                .retrieve()
                .bodyToMono(SkdMeldingerTilTpsRespons.class)
                .block();
    }

}
