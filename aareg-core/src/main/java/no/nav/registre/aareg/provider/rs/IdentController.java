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
import java.util.Map;

import no.nav.registre.aareg.provider.rs.response.SletteArbeidsforholdResponse;
import no.nav.registre.aareg.service.AaregService;
import no.nav.registre.aareg.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;
    private final AaregService aaregService;

    @DeleteMapping
    public SletteArbeidsforholdResponse slettArbeidsforholdFraAaregstub(
            @RequestBody List<String> identer
    ) {
        return identService.slettArbeidsforholdFraAaregstub(identer);
    }

    @GetMapping("/{ident}")
    @ApiOperation(value = "Hent arbeidsforhold fra aareg. Trenger ikke token for å hente.")
    public ResponseEntity<List<Map>> hentArbeidsforhold(
            @PathVariable String ident,
            @RequestParam String miljoe
    ) {
        return aaregService.hentArbeidsforhold(ident, miljoe);
    }
}
