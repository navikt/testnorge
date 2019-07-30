package no.nav.registre.inst.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.inst.Institusjonsforholdsmelding;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
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
    public SletteOppholdResponse slettIdenterFraInst2(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestBody List<String> identer) {
        return identService.slettInstitusjonsforholdTilIdenter(identer, navCallId, navConsumerId);
    }

    @PostMapping()
    public Map<String, List<OppholdResponse>> opprettPersoner(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestBody List<Institusjonsforholdsmelding> institusjonsforholdsmeldinger) {
        return syntetiseringService.opprettInstitusjonsforholdIIInst2(institusjonsforholdsmeldinger, navCallId, navConsumerId);
    }

    @GetMapping()
    public Map<String, List<Institusjonsforholdsmelding>> hentInstitusjonsforholdmeldinger(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam List<String> fnrs) {
        return identService.hentForhold(fnrs, navCallId, navConsumerId);
    }
}
