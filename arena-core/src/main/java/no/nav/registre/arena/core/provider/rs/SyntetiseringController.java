package no.nav.registre.arena.core.provider.rs;


import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.core.provider.rs.requests.SlettArenaRequest;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;


@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    public ResponseEntity<String> registerBrukereIArenaForvalter(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest,
                                                                 @RequestParam Integer antallNyeIdenter) {
        if (antallNyeIdenter != null)
            return registrerBrukereIArenaForvalter(syntetiserArenaRequest, antallNyeIdenter);
        return fyllOppBrukereIArenaForvalter(syntetiserArenaRequest);
    }

    @PostMapping(value = "/slett")
    public ResponseEntity<String> slettBrukereIArenaForvalter(@RequestBody SlettArenaRequest slettArenaRequest) {
        return slettBrukere(slettArenaRequest);
    }


    private ResponseEntity<String> fyllOppBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {

        int antallBrukereAaOpprette = syntetiseringService.getAntallBrukereForAaFylleArenaForvalteren(arenaRequest);

        if (antallBrukereAaOpprette > 0) {
            return registerBrukereIArenaForvalter(arenaRequest, antallBrukereAaOpprette);
        }

        return ResponseEntity.ok(MessageFormat.format(
                "Minst {}%% identer hadde allerede meldekort. Ingen nye identer ble lagt til i Arena Forvalteren",
                (syntetiseringService.getProsentandelSomSkalHaMeldekort() * 100)));
    }

    private ResponseEntity<String> registrerBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest,
                                                                   Integer antallNyeIdenter) {
        StatusFraArenaForvalterResponse response =
                syntetiseringService.sendBrukereTilArenaForvalterConsumer(arenaRequest, antallNyeIdenter);

        return byggOpprettedeBrukereResponse(response);
    }

    // TODO: kanskje returnere et map med både slettede og ikke slettede identer?
    private ResponseEntity<String> slettBrukere(SlettArenaRequest slettArenaRequest) {

        List<String> slettedeIdenter = syntetiseringService.slettBrukereIArenaForvalter(slettArenaRequest);
        List<String> alleIdenter = slettArenaRequest.getIdenter();
        alleIdenter.removeAll(slettedeIdenter);

        StringBuilder responseBody = new StringBuilder();

        responseBody.append(MessageFormat.format("Slettede identer: \n{}\nKunne ikke slette følgende identer: \n{}\n",
                slettedeIdenter.toString(), alleIdenter.toString()));
        return ResponseEntity.ok(responseBody.toString());
    }

    private ResponseEntity<String> byggOpprettedeBrukereResponse(StatusFraArenaForvalterResponse response) {
        StringBuilder status = new StringBuilder();

        if (!CollectionUtils.isEmpty(response.getArbeidsokerList())) {
            status.append("Nye identer opprettet med meldekort: \n")
                    .append(response.getArbeidsokerList().stream().map(Arbeidsoker::getPersonident))
                    .append(". ");
        } else {
            status.append("Fant ingen identer som kunne opprettes i Arena Forvalteren.");
        }

        return ResponseEntity.ok().body(status.toString());
    }

}
