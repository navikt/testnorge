package no.nav.organisasjonforvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.provider.rs.requests.DeployRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse;
import no.nav.organisasjonforvalter.provider.rs.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.service.BestillingService;
import no.nav.organisasjonforvalter.service.DeploymentService;
import no.nav.organisasjonforvalter.service.OrganisasjonService;
import org.springframework.web.bind.annotation.*;

import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;

import java.util.List;

@RestController
@RequestMapping("api/v1/organisasjon")
@RequiredArgsConstructor
public class OrganisasjonController {

    private final BestillingService bestillingService;
    private final DeploymentService deploymentService;
    private final OrganisasjonService organisasjonService;

    @PostMapping("/bestilling")
    public BestillingResponse createOrganisasjon(@RequestBody BestillingRequest request) {

        return bestillingService.execute(request);
    }

    @PostMapping("/deployment")
    public DeployResponse deployOrganisasjon(@RequestBody DeployRequest request) {

        return deploymentService.deploy(request);
    }

    @GetMapping
    public List<RsOrganisasjon> getOrganisasjon(@RequestParam List<String> orgnumre) {

        return organisasjonService.getOrganisasjoner(orgnumre);
    }
}
