package no.nav.registre.testnorge.arena.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.arena.service.RettighetTilleggService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class TilleggstoenadController {

    private final RettighetTilleggService rettighetTilleggService;

    @PostMapping("generer/tillegg/boutgifter")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggBoutgifter(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadBoutgifter(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/dagligReise")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggDagligReise(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadDagligReise(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/flytting")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggFlytting(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadFlytting(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/laeremidler")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggLaeremidler(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadLaeremidler(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/hjemreise")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggHjemreise(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadHjemreise(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/reiseObligatoriskSamling")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggReiseObligatoriskSamling(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadReiseObligatoriskSamling(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/tilsynBarn")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggTilsynBarn(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadTilsynBarn(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/tilsynFamiliemedlemmer")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggTilsynFamiliemedlemmer(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadTilsynFamiliemedlemmer(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/tilsynBarnArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggTilsynBarnArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadTilsynBarnArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/tilsynFamiliemedlemmerArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggTilsynFamiliemedlemmerArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/boutgifterArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggBoutgifterArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadBoutgifterArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/dagligReiseArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggDagligReiseArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadDagligReiseArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/flyttingArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggFlyttingArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadFlyttingArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/laeremidlerArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggLaeremidlerArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadLaeremidlerArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/hjemreiseArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggHjemreiseArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService.opprettTilleggsstoenadHjemreiseArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/reiseObligatoriskSamlingArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggObligatoriskSamlingArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService
                        .opprettTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tillegg/reisestoenadArbeidssoekere")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTilleggReisestoenadArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTilleggService
                        .opprettTilleggsstoenadReisestoenadArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }
}
