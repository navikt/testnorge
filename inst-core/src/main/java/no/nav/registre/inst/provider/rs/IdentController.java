package no.nav.registre.inst.provider.rs;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
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

import java.util.List;
import java.util.Map;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @PostMapping
    @ApiOperation(value = "Her kan man opprette ett institusjonsopphold i inst2.")
    public OppholdResponse opprettInstitusjonsopphold(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam String miljoe,
            @RequestBody Institusjonsopphold institusjonsopphold) {
        return identService.sendTilInst2(navCallId, navConsumerId, miljoe, institusjonsopphold);
    }

    @GetMapping
    @ApiOperation(value = "Her kan man hente alle institusjonsoppholdene tilhørende angitte identer fra inst2.")
    public List<Institusjonsopphold> hentInstitusjonsopphold(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam String miljoe,
            @RequestParam List<String> identer) {
        return identService.hentOppholdTilIdenter(navCallId, navConsumerId, miljoe, identer);
    }

    @PutMapping("/{oppholdId}")
    @ApiOperation(value = "Her kan man oppdatere et institusjonsopphold med angitt oppholdId i inst2.")
    public ResponseEntity oppdaterInstitusjonsopphold(@PathVariable Long oppholdId, @RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam String miljoe,
            @RequestBody Institusjonsopphold institusjonsopphold) {
        return identService.oppdaterInstitusjonsopphold(navCallId, navConsumerId, miljoe, oppholdId, institusjonsopphold);
    }

    @DeleteMapping
    @ApiOperation(value = "Her kan man slette alle institusjonsoppholdene med de angitte oppholdId-ene fra inst2.")
    public Map<Long, ResponseEntity> slettInstitusjonsopphold(@RequestHeader String navCallId, @RequestHeader String navConsumerId,
            @RequestParam String miljoe, @RequestParam List<Long> oppholdIder) {
        Map<String, Object> tokenObject = identService.hentTokenTilInst2();
        Map<Long, ResponseEntity> status = Maps.newHashMapWithExpectedSize(oppholdIder.size());
        for (Long oppholdId : oppholdIder) {
            status.put(oppholdId, identService.slettOppholdMedId(tokenObject, navCallId, navConsumerId, miljoe, oppholdId));
        }
        return status;
    }

    @PostMapping("/batch")
    @ApiOperation(value = "Her kan man opprette flere institusjonsopphold i inst2.")
    public List<OppholdResponse> opprettFlereInstitusjonsopphold(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam String miljoe,
            @RequestBody List<Institusjonsopphold> institusjonsopphold) {
        return identService.opprettInstitusjonsopphold(navCallId, navConsumerId, miljoe, institusjonsopphold);
    }

    @DeleteMapping("/batch")
    @ApiOperation(value = "Her kan man slette alle institusjonsoppholdene til de angitte identene fra inst2.")
    public List<OppholdResponse> slettIdenter(@RequestHeader String navCallId, @RequestHeader String navConsumerId, @RequestParam String miljoe, @RequestParam List<String> identer) {
        return identService.slettInstitusjonsoppholdTilIdenter(navCallId, navConsumerId, miljoe, identer);
    }
}
