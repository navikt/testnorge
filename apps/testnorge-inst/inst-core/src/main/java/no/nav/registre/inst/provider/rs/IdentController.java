package no.nav.registre.inst.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.InstitusjonsoppholdV2;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.service.IdentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;

import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("api/v1")
public class IdentController {

    @Autowired
    private IdentService identService;

    private static final List<String> miljoer;

    static {
        miljoer = Arrays.asList(
                "u5", "u6",
                "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t8",
                "q0", "q1", "q2", "q3", "q4", "q5", "q6", "q8", "qx"
        );
    }

    @PostMapping("/ident")
    @ApiOperation(value = "Her kan man opprette ett institusjonsopphold i inst2.")
    public OppholdResponse opprettInstitusjonsopphold(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestBody InstitusjonsoppholdV2 institusjonsopphold
    ) {
        return identService.sendTilInst2(navCallId, navConsumerId, miljoe, institusjonsopphold);
    }

    @GetMapping("/ident")
    @ApiOperation(value = "Her kan man hente alle institusjonsoppholdene tilhørende angitte identer fra inst2.")
    public List<Institusjonsopphold> hentInstitusjonsopphold(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestParam List<String> identer
    ) {
        return identService.hentOppholdTilIdenter(navCallId, navConsumerId, miljoe, identer);
    }

    @PutMapping("/ident/{oppholdId}")
    @ApiOperation(value = "Her kan man oppdatere et institusjonsopphold med angitt oppholdId i inst2.")
    public ResponseEntity<Object> oppdaterInstitusjonsopphold(
            @PathVariable Long oppholdId,
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestBody Institusjonsopphold institusjonsopphold
    ) {
        return identService.oppdaterInstitusjonsopphold(navCallId, navConsumerId, miljoe, oppholdId, institusjonsopphold);
    }

    @GetMapping("/miljoer")
    @ApiOperation(value = "Her kan man sjekke hvilke miljøer Inst2 er tilgjengelig i.")
    public List<String> hentTilgjengeligeMiljoer() {
        return identService.hentTilgjengeligeMiljoer();
    }

    @PostMapping("/ident/batch")
    @ApiOperation(value = "Her kan man opprette flere institusjonsopphold i inst2.")
    public List<OppholdResponse> opprettFlereInstitusjonsopphold(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestBody List<InstitusjonsoppholdV2> institusjonsopphold
    ) {
        return identService.opprettInstitusjonsopphold(navCallId, navConsumerId, miljoe, institusjonsopphold);
    }

    @DeleteMapping("/ident/batch")
    @ApiOperation(value = "Her kan man slette alle institusjonsoppholdene til de angitte identene fra inst2.")
    public List<OppholdResponse> slettIdenter(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestParam List<String> identer
    ) {
        return identService.slettInstitusjonsoppholdTilIdenter(navCallId, navConsumerId, miljoe, identer);
    }
}
