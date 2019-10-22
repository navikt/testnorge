package no.nav.registre.aareg.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.aareg.service.AaregService;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isReady")
    public String isReady() {
        return "OK";
    }

    @GetMapping("/isAlive")
    public String isAlive() {
        return "OK";
    }

    @Autowired
    private AaregService aaregService;

    @GetMapping("/arb")
    public ResponseEntity hentArbeidsforhold(
            @RequestParam String ident,
            @RequestParam String miljoe
    ) {
        return aaregService.hentArbeidsforhold(ident, miljoe);
    }
}
