package no.nav.registre.testnorge.arena.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.arena.service.RettighetAapService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class AapController {

    private final RettighetAapService rettighetAapService;

    @PostMapping("generer/rettighet/aap")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererRettighetAap(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetAapService.genererAapMedTilhoerende115(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/rettighet/aap/ident/{ident}")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererRettighetAapPaaIdent(
            @PathVariable String ident,
            @RequestParam String miljoe
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetAapService.genererAap(ident, miljoe));
    }

    @PostMapping("generer/rettighet/aap_115")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererRettighetAap115(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetAapService.genererAap115(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/rettighet/ungUfoer")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererRettighetUngUfoer(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetAapService.genererUngUfoer(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/rettighet/tvungenForvaltning")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererRettighetTvungenForvaltning(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetAapService.genererTvungenForvaltning(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/rettighet/fritakMeldekort")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererRettighetFritakMeldekort(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetAapService.genererFritakMeldekort(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }
}
