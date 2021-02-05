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

import no.nav.registre.testnorge.arena.service.RettighetTiltakService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class TiltakController {

    private final RettighetTiltakService rettighetTiltakService;

    @PostMapping("generer/tiltaksdeltakelse")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTiltaksdeltakelse(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTiltakService.opprettTiltaksdeltakelse(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tiltakspenger")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTiltakspenger(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTiltakService.opprettTiltakspenger(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/barnetillegg")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererBarnetillegg(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTiltakService.opprettBarnetillegg(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

    @PostMapping("generer/tiltaksaktivitet")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererTiltaksaktivitet(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rettighetTiltakService.opprettTiltaksaktivitet(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }

}
