package no.nav.organisasjon-forvalter

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;

@RestController
@RequestMapping("api/v1/bestilling")
public class BestillingController {

    @PostMapping
    public BestillingResponse acquireOrganisasjon(BestillingRequest request) {


    }
}
