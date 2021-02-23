package no.nav.registre.testnorge.synt.tiltakservice.consumer;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.RettighetRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.synt.tiltakservice.consumer.command.GetArenaBrukerCommand;
import no.nav.registre.testnorge.synt.tiltakservice.consumer.command.PostArenaBrukerCommand;
import no.nav.registre.testnorge.synt.tiltakservice.consumer.command.PostFinnTiltakCommand;
import no.nav.registre.testnorge.synt.tiltakservice.consumer.command.PostRettighetCommand;

@Slf4j
@Component
@DependencyOn(value = "arena-forvalteren", external = true)
public class ArenaForvalterConsumer {

    private final WebClient webClient;

    private static final String EIER = "Dolly";

    public ArenaForvalterConsumer(
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaForvalterServerUrl).build();
    }

    public NyttVedtakResponse finnTiltak(RettighetFinnTiltakRequest rettighet) {
        return new PostFinnTiltakCommand(rettighet, webClient).call();
    }

    public NyttVedtakResponse opprettRettighet(RettighetRequest rettighet) {
        var response = new PostRettighetCommand(rettighet, webClient).call();

        if (response == null || (response.getFeiledeRettigheter() != null && !response.getFeiledeRettigheter().isEmpty())) {
            log.info("Innsendt rettighet feilet for ident: " + rettighet.getPersonident());
        }
        return response;
    }

    public NyeBrukereResponse hentDollyArbeidssoekerIArena(String ident, String miljoe) {
        return new GetArenaBrukerCommand(ident, miljoe, EIER, webClient).call();
    }

    public NyeBrukereResponse sendTilArenaForvalter(
            List<NyBruker> nyeBrukere
    ) {
        return new PostArenaBrukerCommand(nyeBrukere, EIER, webClient).call();
    }
}
