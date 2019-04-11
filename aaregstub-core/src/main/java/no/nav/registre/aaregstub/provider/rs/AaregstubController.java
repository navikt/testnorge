package no.nav.registre.aaregstub.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.aaregstub.arbeidsforhold.ArbeidsforholdsResponse;
import no.nav.registre.aaregstub.arbeidsforhold.Ident;
import no.nav.registre.aaregstub.arbeidsforhold.consumer.rs.responses.DollyResponse;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsforhold;
import no.nav.registre.aaregstub.provider.rs.responses.StatusResponse;
import no.nav.registre.aaregstub.service.ArbeidsforholdService;

@RestController
@RequestMapping("api/v1")
public class AaregstubController {

    @Autowired
    private ArbeidsforholdService arbeidsforholdService;

    @LogExceptions
    @PostMapping(value = "/lagreArbeidsforhold")
    public StatusResponse lagreArbeidsforhold(@RequestParam("lagreIAareg") Boolean lagreIAareg, @RequestBody List<ArbeidsforholdsResponse> arbeidsforholdsmeldinger) {
        return arbeidsforholdService.lagreArbeidsforhold(arbeidsforholdsmeldinger, lagreIAareg);
    }

    @LogExceptions
    @GetMapping(value = "/hentIdentMedArbeidsforhold/{ident}")
    public Ident hentIdentMedArbeidsforhold(@PathVariable String ident) {
        Optional<Ident> identMedArbeidsforhold = arbeidsforholdService.hentIdentMedArbeidsforholdNy(ident);
        return identMedArbeidsforhold.orElse(null);
    }

    @LogExceptions
    @GetMapping(value = "/hentArbeidsforholdNy/{id}")
    public Arbeidsforhold hentArbeidsforhold(@PathVariable Long id) {
        Optional<Arbeidsforhold> arbeidsforhold = arbeidsforholdService.hentArbeidsforhold(id);
        return arbeidsforhold.orElse(null);
    }

    @LogExceptions
    @GetMapping(value = "/hentAlleArbeidstakere")
    public List<String> hentAlleArbeidstakere() {
        return arbeidsforholdService.hentAlleArbeidstakere();
    }

    @LogExceptions
    @Transactional
    @DeleteMapping(value = "/slettArbeidsforhold/{id}")
    public void slettArbeidsforhold(@PathVariable Long id) {
        arbeidsforholdService.slettArbeidsforhold(id);
    }

    @LogExceptions
    @PostMapping(value = "/sendArbeidsforholdTilAareg")
    public List<DollyResponse> sendArbeidsforholdTilAareg(@RequestBody List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold) {
        return arbeidsforholdService.sendArbeidsforholdTilAareg(syntetiserteArbeidsforhold);
    }

    @LogExceptions
    @GetMapping(value = "hentArbeidsforholdFraAareg")
    public Object hentArbeidsforholdFraAareg(@RequestParam String ident, @RequestParam String miljoe) {
        return arbeidsforholdService.hentArbeidsforholdFraAareg(ident, miljoe);
    }
}
