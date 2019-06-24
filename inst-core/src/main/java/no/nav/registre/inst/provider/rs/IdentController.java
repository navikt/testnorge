package no.nav.registre.inst.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.inst.Institusjonsforholdsmelding;
import no.nav.registre.inst.provider.rs.responses.SletteOppholdResponse;
import no.nav.registre.inst.service.IdentService;
import no.nav.registre.inst.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @Autowired
    private SyntetiseringService syntetiseringService;

    @LogExceptions
    @DeleteMapping()
    public SletteOppholdResponse slettIdenterFraInst2(@RequestBody List<String> identer) {
        return identService.slettInstitusjonsforholdTilIdenter(identer);
    }

    @PostMapping()
    public List<ResponseEntity> opprettPersoner(@RequestBody List<Institusjonsforholdsmelding> institusjonsforholdsmeldinger) {
        return syntetiseringService.opprettInstitusjonsoppholdIInst2(institusjonsforholdsmeldinger);
    }
}
