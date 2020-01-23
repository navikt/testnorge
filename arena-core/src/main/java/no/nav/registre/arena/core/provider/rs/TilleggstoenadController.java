package no.nav.registre.arena.core.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.RettighetTilleggService;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class TilleggstoenadController {

    private final RettighetTilleggService rettighetTilleggService;

    @PostMapping("generer/tillegg/laeremidler")
    public List<NyttVedtakResponse> genererTilleggLaeremidler(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadLaeremidler(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/boutgifter")
    public List<NyttVedtakResponse> genererTilleggBoutgifter(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadBoutgifter(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/flytting")
    public List<NyttVedtakResponse> genererTilleggFlytting(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadFlytting(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/hjemreise")
    public List<NyttVedtakResponse> genererTilleggHjemreise(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadHjemreise(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/reisestoenadArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggReisestoenadArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService
                .opprettTilleggsstoenadReisestoenadArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/boutgifterArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggBoutgifterArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadBoutgifterArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/dagligReise")
    public List<NyttVedtakResponse> genererTilleggDagligReise(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettDagligReise(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/reiseObligatoriskSamling")
    public List<NyttVedtakResponse> genererTilleggReiseObligatoriskSamling(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettReiseObligatoriskSamling(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }
}
