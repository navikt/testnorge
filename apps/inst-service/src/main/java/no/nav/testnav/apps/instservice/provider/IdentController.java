package no.nav.testnav.apps.instservice.provider;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;

import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.provider.responses.OppholdResponse;
import no.nav.testnav.apps.instservice.service.IdentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @PostMapping("/ident")
    @Operation(description = "Her kan man opprette ett institusjonsopphold i inst2.")
    public OppholdResponse opprettInstitusjonsopphold(
            @RequestParam String miljoe,
            @RequestBody InstitusjonsoppholdV2 institusjonsopphold
    ) {
        return identService.sendTilInst2(miljoe, institusjonsopphold);
    }

    @GetMapping("/ident")
    @Operation(description = "Her kan man hente alle institusjonsoppholdene tilhørende angitte identer fra inst2.")
    public List<InstitusjonsoppholdV2> hentInstitusjonsopphold(
            @RequestParam String miljoe,
            @RequestParam List<String> identer
    ) {
        return identService.hentOppholdTilIdenter(miljoe, identer);
    }

    @GetMapping("/miljoer")
    @Operation(description = "Her kan man sjekke hvilke miljøer Inst2 er tilgjengelig i.")
    public List<String> hentTilgjengeligeMiljoer() {
        return identService.hentTilgjengeligeMiljoer();
    }

    @PostMapping("/ident/batch")
    @Operation(description = "Her kan man opprette flere institusjonsopphold i inst2.")
    public List<OppholdResponse> opprettFlereInstitusjonsopphold(
            @RequestParam String miljoe,
            @RequestBody List<InstitusjonsoppholdV2> institusjonsopphold
    ) {
        return identService.opprettInstitusjonsopphold(miljoe, institusjonsopphold);
    }

    @DeleteMapping("/ident/batch")
    @Operation(description = "Her kan man slette alle institusjonsoppholdene til de angitte identene fra inst2.")
    public List<OppholdResponse> slettIdenter(
            @RequestParam String miljoe,
            @RequestParam List<String> identer
    ) {
        return identService.slettInstitusjonsoppholdTilIdenter(miljoe, identer);
    }
}
