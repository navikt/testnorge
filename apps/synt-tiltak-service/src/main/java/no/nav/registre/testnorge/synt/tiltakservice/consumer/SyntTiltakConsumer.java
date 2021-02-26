package no.nav.registre.testnorge.synt.tiltakservice.consumer;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.SyntRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.synt.tiltakservice.consumer.command.PostSyntTiltakRequestCommand;

@Component
@DependencyOn(value = "nais-synthdata-arena-tiltak", external = true)
public class SyntTiltakConsumer {
    private final WebClient webClient;

    private static final String ARENA_TILTAKSPENGER_URL = "/v1/arena/tiltak/basi";

    public SyntTiltakConsumer(
            @Value("${synt-arena-tiltak.rest-api.url}") String arenaTiltakServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaTiltakServerUrl).build();
    }

    public List<NyttVedtakTiltak> getSyntetiskTiltakspenger(List<SyntRequest> syntRequest) {
        return new PostSyntTiltakRequestCommand(webClient, syntRequest, ARENA_TILTAKSPENGER_URL).call();
    }

}
