package no.nav.registre.aareg.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.aareg.consumer.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @LogExceptions
    @PostMapping(value = "/generer")
    public ResponseEntity genererMeldinger(@RequestBody SyntetiserAaregRequest syntetiserAaregRequest) {
        syntetiseringService.hentArbeidshistorikk(syntetiserAaregRequest);
        return null;
    }
}
