package no.nav.organisasjonforvalter.controller;

import static java.util.Objects.nonNull;
import static no.nav.organisasjonforvalter.config.CacheConfig.CACHE_BEDRIFT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nav.organisasjonforvalter.dto.requests.BestillingRequest;
import no.nav.organisasjonforvalter.dto.requests.DeployRequest;
import no.nav.organisasjonforvalter.dto.responses.BestillingResponse;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.dto.responses.UnderenhetResponse;
import no.nav.organisasjonforvalter.service.BestillingService;
import no.nav.organisasjonforvalter.service.DrivervirksomheterService;
import no.nav.organisasjonforvalter.service.ImportService;
import no.nav.organisasjonforvalter.service.OrdreService;
import no.nav.organisasjonforvalter.service.OrdreStatusService;
import no.nav.organisasjonforvalter.service.OrganisasjonService;
import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedId;

@RestController
@RequestMapping("api/v2/organisasjoner")
@RequiredArgsConstructor
public class OrganisasjonController {

    private final BestillingService bestillingService;
    private final OrdreService ordreService;
    private final OrdreStatusService ordreStatusService;
    private final OrganisasjonService organisasjonService;
    private final ImportService importService;
    private final DrivervirksomheterService drivervirksomheterService;
    private final GetAuthenticatedId getAuthenticatedId;

    @CacheEvict(value = CACHE_BEDRIFT, allEntries = true)
    @PostMapping
    @Operation(description = "Opprett organisasjon med angitte egenskaper")
    public BestillingResponse createOrganisasjon(@RequestBody BestillingRequest request) {

        return bestillingService.execute(request);
    }

    @PostMapping("/ordre")
    @Operation(description = "Send organisasjoner til EREG i angitte miljøer")
    public DeployResponse deployOrganisasjon(@RequestBody DeployRequest request) {

        return ordreService.deploy(request);
    }

    @GetMapping("/ordrestatus")
    @Operation(description = "Sjekk deploy status")
    public OrdreResponse getStatus(@RequestParam List<String> orgnumre) {

        return ordreStatusService.getStatus(orgnumre);
    }

    @GetMapping
    @Operation(description = "Hent organisasjon fra database (org-forvalter)")
    public List<RsOrganisasjon> getOrganisasjon(@RequestParam List<String> orgnumre) {

        return organisasjonService.getOrganisasjoner(orgnumre);
    }

    @GetMapping("/alle")
    @Operation(description = "Hent organisasjoner for brukerid fra database (org-forvalter)")
    public List<RsOrganisasjon> getOrganisasjoner(@Parameter(description = "BrukerId fra Azure") @RequestParam(required = false) String brukerid) {
        var id = getAuthenticatedId.call();
        return organisasjonService.getOrganisasjoner(nonNull(brukerid) ? brukerid : id);
    }

    @GetMapping("/framiljoe")
    @Operation(description = "Hent organisasjon fra EREG")
    public Map<String, RsOrganisasjon> importOrganisasjon(@RequestParam String orgnummer,
                                                          @RequestParam(required = false) Set<String> miljoer) {

        return importService.getOrganisasjoner(orgnummer, miljoer);
    }

    @GetMapping("/virksomheter")
    @Operation(description = "Hent virksomheter av type BEDR og AAFY fra database. Kun disse typer kan ha ansatte")
    public List<UnderenhetResponse> getUnderenheter(@Parameter(description = "BrukerId fra Azure") @RequestParam(required = false) String brukerid) {
        var id = getAuthenticatedId.call();
        return drivervirksomheterService.getUnderenheter(nonNull(brukerid) ? brukerid : id);
    }
}
