package no.nav.registre.arena.core.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.arena.core.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.RettighetAapService;

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

    @PostMapping("generer/rettighet/aap/ident/{ident}")
    public List<NyttVedtakResponse> genererRettighetAapPaaIdent(
            @PathVariable String ident,
            @RequestParam String miljoe
    ) {
        return rettighetAapService.genererAapMedTilhoerende115(ident, miljoe);
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
