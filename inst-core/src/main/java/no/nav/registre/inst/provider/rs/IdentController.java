package no.nav.registre.inst.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.provider.rs.responses.SletteOppholdResponse;
import no.nav.registre.inst.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @PostMapping
    public OppholdResponse opprettInstitusjonsopphold(@RequestHeader String navCallId, @RequestHeader String navConsumerId,
            @RequestBody Institusjonsopphold institusjonsopphold) {
        return identService.sendTilInst2(institusjonsopphold, navCallId, navConsumerId);
    }

    @GetMapping
    public Map<String, List<Institusjonsopphold>> hentInstitusjonsopphold(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam List<String> fnrs) {
        return identService.hentForhold(fnrs, navCallId, navConsumerId);
    }

    @PutMapping("/{oppholdId}")
    public ResponseEntity oppdaterInstitusjonsopphold(@PathVariable Long oppholdId, @RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestBody Institusjonsopphold institusjonsopphold) {
        return identService.oppdaterInstitusjonsopphold(navCallId, navConsumerId, oppholdId, institusjonsopphold);
    }

    @DeleteMapping
    public Map<Long, ResponseEntity> slettInstitusjonsopphold(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam List<Long> oppholdIder) {
        Map<String, Object> tokenObject = identService.hentTokenTilInst2();
        Map<Long, ResponseEntity> status = new HashMap<>();
        for (Long oppholdId : oppholdIder) {
            status.put(oppholdId, identService.slettOppholdMedId(tokenObject, navCallId, navConsumerId, oppholdId));
        }
        return status;
    }

    @PostMapping("/batch")
    public Map<String, List<OppholdResponse>> opprettFlereInstitusjonsopphold(@RequestHeader String navCallId, @RequestHeader String navConsumerId,
            @RequestBody List<Institusjonsopphold> institusjonsopphold) {
        return identService.opprettInstitusjonsopphold(institusjonsopphold, navCallId, navConsumerId);
    }

    @DeleteMapping("/batch")
    public SletteOppholdResponse slettIdenter(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam List<String> identer) {
        return identService.slettInstitusjonsforholdTilIdenter(identer, navCallId, navConsumerId);
    }
}
