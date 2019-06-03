package no.nav.registre.aareg.provider.rs;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;
import no.nav.registre.aareg.consumer.rs.responses.StatusFraAaregstubResponse;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @LogExceptions
    @PostMapping(value = "/generer")
    public ResponseEntity genererArbeidsforholdsmeldinger(@RequestParam("lagreIAareg") Boolean lagreIAareg, @RequestBody SyntetiserAaregRequest syntetiserAaregRequest) {
        return syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, lagreIAareg);
    }

    @LogExceptions
    @PostMapping(value = "/sendTilAareg")
    public StatusFraAaregstubResponse sendArbeidsforholdTilAareg(@RequestParam(required = false, defaultValue = "false") Boolean fyllUtArbeidsforhold, @RequestBody List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold) {
        return syntetiseringService.sendArbeidsforholdTilAareg(syntetiserteArbeidsforhold, fyllUtArbeidsforhold);
    }
}
