package no.nav.registre.arena.core.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.arena.core.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.RettighetTilleggService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class TilleggstoenadController {

    private final RettighetTilleggService rettighetTilleggService;

    @PostMapping("generer/tillegg/boutgifter")
    public List<NyttVedtakResponse> genererTilleggBoutgifter(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadBoutgifter(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/dagligReise")
    public List<NyttVedtakResponse> genererTilleggDagligReise(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadDagligReise(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/flytting")
    public List<NyttVedtakResponse> genererTilleggFlytting(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadFlytting(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/laeremidler")
    public List<NyttVedtakResponse> genererTilleggLaeremidler(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadLaeremidler(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/hjemreise")
    public List<NyttVedtakResponse> genererTilleggHjemreise(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadHjemreise(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/reiseObligatoriskSamling")
    public List<NyttVedtakResponse> genererTilleggReiseObligatoriskSamling(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadReiseObligatoriskSamling(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/tilsynBarn")
    public List<NyttVedtakResponse> genererTilleggTilsynBarn(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadTilsynBarn(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/tilsynFamiliemedlemmer")
    public List<NyttVedtakResponse> genererTilleggTilsynFamiliemedlemmer(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadTilsynFamiliemedlemmer(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/tilsynBarnArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggTilsynBarnArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadTilsynBarnArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/tilsynFamiliemedlemmerArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggTilsynFamiliemedlemmerArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService
                .opprettTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/boutgifterArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggBoutgifterArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadBoutgifterArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/dagligReiseArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggDagligReiseArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadDagligReiseArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/flyttingArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggFlyttingArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadFlyttingArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/laeremidlerArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggLaeremidlerArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadLaeremidlerArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/hjemreiseArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggHjemreiseArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadHjemreiseArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/reiseObligatoriskSamlingArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggObligatoriskSamlingArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService
                .opprettTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/reisestoenadArbeidssoekere")
    public List<NyttVedtakResponse> genererTilleggReisestoenadArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService
                .opprettTilleggsstoenadReisestoenadArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }
}
