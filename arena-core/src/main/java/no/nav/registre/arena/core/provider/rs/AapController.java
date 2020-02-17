package no.nav.registre.arena.core.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.arena.core.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.RettighetAapService;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class AapController {

    private final RettighetAapService rettighetAapService;

    @PostMapping("generer/rettighet/aap")
    public List<NyttVedtakResponse> genererRettighetAap(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetAapService.genererAapMedTilhoerende115(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/rettighet/aap_115")
    public List<NyttVedtakResponse> genererRettighetAap115(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetAapService.genererAap115(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/rettighet/ungUfoer")
    public List<NyttVedtakResponse> genererRettighetUngUfoer(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetAapService.genererUngUfoer(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/rettighet/tvungenForvaltning")
    public List<NyttVedtakResponse> genererRettighetTvungenForvaltning(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetAapService.genererTvungenForvaltning(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/rettighet/fritakMeldekort")
    public List<NyttVedtakResponse> genererRettighetFritakMeldekort(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetAapService.genererFritakMeldekort(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }
}
