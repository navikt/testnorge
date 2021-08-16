package no.nav.registre.inst.provider.rs;

import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    public Map<String, List<OppholdResponse>> genererInstitusjonsmeldinger(
            @RequestHeader(HEADER_NAV_CALL_ID) @NotBlank String navCallId,
            @RequestHeader(HEADER_NAV_CONSUMER_ID) @NotBlank String navConsumerId,
            @RequestParam String miljoe,
            @RequestBody SyntetiserInstRequest syntetiserInstRequest
    ) {
        return syntetiseringService.finnSyntetiserteMeldingerOgLagreIInst2(navCallId, navConsumerId, miljoe, syntetiserInstRequest);
    }
}