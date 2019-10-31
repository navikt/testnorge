package no.nav.registre.aareg.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @LogExceptions
    @PostMapping("/generer")
    public ResponseEntity genererArbeidsforholdsmeldinger(
            @RequestParam(defaultValue = "true") Boolean sendAlleEksisterende,
            @RequestBody SyntetiserAaregRequest syntetiserAaregRequest
    ) {
        return syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, sendAlleEksisterende);
    }
}
