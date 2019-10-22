package no.nav.registre.aareg.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.aareg.provider.rs.response.SletteArbeidsforholdResponse;
import no.nav.registre.aareg.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @DeleteMapping
    public SletteArbeidsforholdResponse slettArbeidsforholdFraAaregstub(@RequestBody List<String> identer) {
        return identService.slettArbeidsforholdFraAaregstub(identer);
    }
}
