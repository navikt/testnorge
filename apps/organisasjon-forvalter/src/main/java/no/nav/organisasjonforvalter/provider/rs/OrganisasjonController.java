package no.nav.organisasjonforvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.requests.DeployRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse;
import no.nav.organisasjonforvalter.provider.rs.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.service.BestillingService;
import no.nav.organisasjonforvalter.service.DeploymentService;
import no.nav.organisasjonforvalter.service.ImportService;
import no.nav.organisasjonforvalter.service.OrganisasjonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/organisasjon")
@RequiredArgsConstructor
public class OrganisasjonController {

    private final BestillingService bestillingService;
    private final DeploymentService deploymentService;
    private final OrganisasjonService organisasjonService;
    private final ImportService importService;

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

    @GetMapping("/import")
    public Map<String, RsOrganisasjon> importOrganisasjon(@RequestParam String orgnummer,
                                                          @RequestParam(required = false) List<String> miljoer){

        return importService.getOrganisasjoner(orgnummer, miljoer);
    }
}
