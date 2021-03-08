package no.nav.organisasjonforvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.requests.DeployRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse;
import no.nav.organisasjonforvalter.provider.rs.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.provider.rs.responses.UnderenhetResponse;
import no.nav.organisasjonforvalter.service.BestillingService;
import no.nav.organisasjonforvalter.service.DeploymentService;
import no.nav.organisasjonforvalter.service.ImportService;
import no.nav.organisasjonforvalter.service.OrganisasjonService;
import no.nav.organisasjonforvalter.service.UnderenhetService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.organisasjonforvalter.config.CacheConfig.CACHE_BEDRIFT;
import static no.nav.organisasjonforvalter.config.CacheConfig.CACHE_EREG_IMPORT;

@RestController
@RequestMapping("api/v1/organisasjon")
@RequiredArgsConstructor
public class OrganisasjonController {

    private final BestillingService bestillingService;
    private final DeploymentService deploymentService;
    private final OrganisasjonService organisasjonService;
    private final ImportService importService;
    private final UnderenhetService underenhetService;

    @CacheEvict(value = CACHE_BEDRIFT, allEntries = true)
    @PostMapping("/bestilling")
    public BestillingResponse createOrganisasjon(@RequestBody BestillingRequest request) {

        return bestillingService.execute(request);
    }

    @CacheEvict(value = CACHE_EREG_IMPORT, allEntries = true)
    @PostMapping("/deployment")
    public DeployResponse deployOrganisasjon(@RequestBody DeployRequest request) {

        return deploymentService.deploy(request);
    }

    @GetMapping
    public List<RsOrganisasjon> getOrganisasjon(@RequestParam List<String> orgnumre) {

        return organisasjonService.getOrganisasjoner(orgnumre);
    }

    @Cacheable(CACHE_EREG_IMPORT)
    @GetMapping("/import")
    public Map<String, RsOrganisasjon> importOrganisasjon(@RequestParam String orgnummer,
                                                          @RequestParam(required = false) Set<String> miljoer){

        return importService.getOrganisasjoner(orgnummer, miljoer);
    }

    @Cacheable(CACHE_BEDRIFT)
    @GetMapping("/underenheter")
    public List<UnderenhetResponse> getUnderenheter(@RequestParam String brukerid){

        return underenhetService.getUnderenheter(brukerid);
    }
}
