package no.nav.registre.testnorge.arena.consumer.rs;

import no.nav.registre.testnorge.arena.consumer.rs.command.synt.PostSyntTiltakRequestCommand;
import no.nav.registre.testnorge.arena.consumer.rs.request.synt.SyntRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class TiltakSyntConsumer {

    private final WebClient webClient;

    private static final String ARENA_TILTAKSAKTIVITET_URL = "/v1/arena/tiltak/aktivitet";

    public TiltakSyntConsumer(
            @Value("${synt-arena-tiltak.rest-api.url}") String arenaTiltakServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaTiltakServerUrl).build();
    }

    public List<NyttVedtakTiltak> opprettTiltaksaktivitet(List<SyntRequest> syntRequest) {
        return new PostSyntTiltakRequestCommand(webClient, syntRequest, ARENA_TILTAKSAKTIVITET_URL).call();
    }
}
