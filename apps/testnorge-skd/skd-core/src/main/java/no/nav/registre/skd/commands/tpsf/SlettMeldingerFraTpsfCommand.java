package no.nav.registre.skd.commands.tpsf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SlettMeldingerFraTpsfCommand implements Callable<Boolean> {
    private final WebClient webClient;
    private final List<Long> meldingIder;

    @Override
    public Boolean call() {
        log.info("Sletter {} melding(er) fra TPSF.", meldingIder.size());
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/endringsmelding/deletemeldinger")
                        .build())
                .body(BodyInserters.fromValue(meldingIder))
                .exchange()
                .map(response -> response.statusCode().is2xxSuccessful())
                .block();
    }
}
