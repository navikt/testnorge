package no.nav.registre.aareg.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.aareg.provider.rs.response.SletteArbeidsforholdResponse;
import no.nav.registre.aareg.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @DeleteMapping
    public SletteArbeidsforholdResponse slettArbeidsforholdFraAaregstub(
            @RequestBody List<String> identer
    ) {
        return identService.slettArbeidsforholdFraAaregstub(identer);
    }
}
