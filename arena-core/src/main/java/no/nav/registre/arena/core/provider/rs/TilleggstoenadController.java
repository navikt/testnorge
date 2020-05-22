package no.nav.registre.arena.core.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.registre.arena.core.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.RettighetTilleggService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class TilleggstoenadController {

    private final RettighetTilleggService rettighetTilleggService;

    @PostMapping("generer/tillegg/boutgifter")
    public Map<String, List<NyttVedtakResponse>> genererTilleggBoutgifter(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadBoutgifter(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/dagligReise")
    public Map<String, List<NyttVedtakResponse>> genererTilleggDagligReise(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadDagligReise(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/flytting")
    public Map<String, List<NyttVedtakResponse>> genererTilleggFlytting(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadFlytting(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/laeremidler")
    public Map<String, List<NyttVedtakResponse>> genererTilleggLaeremidler(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadLaeremidler(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/hjemreise")
    public Map<String, List<NyttVedtakResponse>> genererTilleggHjemreise(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadHjemreise(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/reiseObligatoriskSamling")
    public Map<String, List<NyttVedtakResponse>> genererTilleggReiseObligatoriskSamling(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadReiseObligatoriskSamling(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/tilsynBarn")
    public Map<String, List<NyttVedtakResponse>> genererTilleggTilsynBarn(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadTilsynBarn(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/tilsynFamiliemedlemmer")
    public Map<String, List<NyttVedtakResponse>> genererTilleggTilsynFamiliemedlemmer(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadTilsynFamiliemedlemmer(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/tilsynBarnArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggTilsynBarnArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadTilsynBarnArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/tilsynFamiliemedlemmerArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggTilsynFamiliemedlemmerArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService
                .opprettTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/boutgifterArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggBoutgifterArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadBoutgifterArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/dagligReiseArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggDagligReiseArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadDagligReiseArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/flyttingArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggFlyttingArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadFlyttingArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/laeremidlerArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggLaeremidlerArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadLaeremidlerArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/hjemreiseArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggHjemreiseArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService.opprettTilleggsstoenadHjemreiseArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/reiseObligatoriskSamlingArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggObligatoriskSamlingArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService
                .opprettTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tillegg/reisestoenadArbeidssoekere")
    public Map<String, List<NyttVedtakResponse>> genererTilleggReisestoenadArbeidssoekere(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTilleggService
                .opprettTilleggsstoenadReisestoenadArbeidssoekere(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }
}
