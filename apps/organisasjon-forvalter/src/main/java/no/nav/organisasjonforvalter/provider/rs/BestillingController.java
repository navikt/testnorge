package no.nav.organisasjonforvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.service.BestillingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;

@RestController
@RequestMapping("api/v1/bestilling")
@RequiredArgsConstructor
public class BestillingController {

    private final BestillingService bestillingService;

    @PostMapping
    public BestillingResponse createOrganisasjon(BestillingRequest request) {

        return bestillingService.execute(request);
    }
}
