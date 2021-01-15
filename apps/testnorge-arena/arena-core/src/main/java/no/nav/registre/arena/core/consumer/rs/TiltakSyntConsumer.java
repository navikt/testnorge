package no.nav.registre.arena.core.consumer.rs;

import no.nav.registre.arena.core.consumer.rs.command.PostSyntTiltakRequestCommand;
import no.nav.registre.arena.core.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@DependencyOn(value = "nais-synthdata-arena-tiltak", external = true)
public class TiltakSyntConsumer {

    private final WebClient webClient;

    private static final String ARENA_TILTAKSDELTAKELSE_URL = "/v1/arena/tiltak/deltakelse";
    private static final String ARENA_TILTAKSPENGER_URL = "/v1/arena/tiltak/basi";
    private static final String ARENA_BARNETILLEGG_URL = "/v1/arena/tiltak/btil";
    private static final String ARENA_DELTAKERSTATUS_URL = "/v1/arena/tiltak/deltakerstatus";
    private static final String ARENA_TILTAKSAKTIVITET_URL = "/v1/arena/tiltak/aktivitet";

    public TiltakSyntConsumer(
            @Value("${synt-arena-tiltak.rest-api.url}") String arenaTiltakServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaTiltakServerUrl).build();
    }

    public List<NyttVedtakTiltak> opprettTiltaksdeltakelse(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTiltakRequestCommand(webClient, syntRequest, ARENA_TILTAKSDELTAKELSE_URL).call();
    }

    public List<NyttVedtakTiltak> opprettTiltakspenger(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTiltakRequestCommand(webClient, syntRequest, ARENA_TILTAKSPENGER_URL).call();
    }

    public List<NyttVedtakTiltak> opprettBarnetillegg(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTiltakRequestCommand(webClient, syntRequest, ARENA_BARNETILLEGG_URL).call();
    }

    public List<NyttVedtakTiltak> opprettDeltakerstatus(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTiltakRequestCommand(webClient, syntRequest, ARENA_DELTAKERSTATUS_URL).call();
    }

    public List<NyttVedtakTiltak> opprettTiltaksaktivitet(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTiltakRequestCommand(webClient, syntRequest, ARENA_TILTAKSAKTIVITET_URL).call();
    }
}
