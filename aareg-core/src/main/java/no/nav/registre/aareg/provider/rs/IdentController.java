package no.nav.registre.aareg.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.aareg.provider.rs.response.SletteArbeidsforholdResponse;
import no.nav.registre.aareg.service.AaregService;
import no.nav.registre.aareg.service.IdentService;
import no.nav.registre.aareg.service.SyntetiseringService;
import no.nav.tjenester.aordningen.arbeidsforhold.v1.Arbeidsforhold;

@RestController
@RequestMapping("api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;
    private final AaregService aaregService;
    private final SyntetiseringService syntetiseringService;

    @DeleteMapping
    public SletteArbeidsforholdResponse slettArbeidsforholdFraAaregstub(
            @RequestBody List<String> identer
    ) {
        return identService.slettArbeidsforholdFraAaregstub(identer);
    }

    @GetMapping("/{ident}")
    @ApiOperation(value = "Hent arbeidsforhold fra aareg. Trenger ikke token for å hente.")
    public ResponseEntity<List<Arbeidsforhold>> hentArbeidsforhold(
            @PathVariable String ident,
            @RequestParam String miljoe
    ) {
        return aaregService.hentArbeidsforhold(ident, miljoe);
    }

    @LogExceptions
    @GetMapping(value = "/avspillergruppe/{avspillergruppeId}")
    public Set<String> hentIdenterIAvspillergruppeMedArbeidsforhold(
            @PathVariable Long avspillergruppeId,
            @RequestParam String miljoe,
            @RequestParam(required = false, defaultValue = "false") Boolean validerMotAareg
    ) {
        return syntetiseringService.hentIdenterIAvspillergruppeMedArbeidsforhold(avspillergruppeId, miljoe, validerMotAareg);
    }
}
