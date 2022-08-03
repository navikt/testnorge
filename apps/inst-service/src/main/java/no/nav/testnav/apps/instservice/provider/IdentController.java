package no.nav.testnav.apps.instservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.provider.responses.OppholdResponse;
import no.nav.testnav.apps.instservice.service.IdentService;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;


@Slf4j
@RestController
@CrossOrigin
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @PostMapping("/ident")
//    @ApiOperation(value = "Her kan man opprette ett institusjonsopphold i inst2.")
    public OppholdResponse opprettInstitusjonsopphold(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestBody InstitusjonsoppholdV2 institusjonsopphold
    ) {
        return identService.sendTilInst2(navCallId, navConsumerId, miljoe, institusjonsopphold);
    }

    @GetMapping("/ident")
//    @ApiOperation(value = "Her kan man hente alle institusjonsoppholdene tilhørende angitte identer fra inst2.")
    public List<InstitusjonsoppholdV2> hentInstitusjonsopphold(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestParam List<String> identer
    ) {
        return identService.hentOppholdTilIdenter(navCallId, navConsumerId, miljoe, identer);
    }

    @GetMapping("/miljoer")
//    @ApiOperation(value = "Her kan man sjekke hvilke miljøer Inst2 er tilgjengelig i.")
    public List<String> hentTilgjengeligeMiljoer() {
        return identService.hentTilgjengeligeMiljoer();
    }

    @PostMapping("/ident/batch")
//    @ApiOperation(value = "Her kan man opprette flere institusjonsopphold i inst2.")
    public List<OppholdResponse> opprettFlereInstitusjonsopphold(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestBody List<InstitusjonsoppholdV2> institusjonsopphold
    ) {
        return identService.opprettInstitusjonsopphold(navCallId, navConsumerId, miljoe, institusjonsopphold);
    }

    @DeleteMapping("/ident/batch")
//    @ApiOperation(value = "Her kan man slette alle institusjonsoppholdene til de angitte identene fra inst2.")
    public List<OppholdResponse> slettIdenter(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestParam List<String> identer
    ) {
        return identService.slettInstitusjonsoppholdTilIdenter(navCallId, navConsumerId, miljoe, identer);
    }
}
