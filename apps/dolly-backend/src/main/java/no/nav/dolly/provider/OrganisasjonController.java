package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonClient;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.service.OrganisasjonBestillingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static no.nav.dolly.provider.OrganisasjonBestillingController.getStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/organisasjon")
public class OrganisasjonController {

    private final OrganisasjonClient organisasjonClient;
    private final OrganisasjonBestillingService bestillingService;

    @PutMapping("/gjenopprett/{bestillingId}")
    @Operation(description = "Gjenopprett organisasjon")
    public RsOrganisasjonBestillingStatus gjenopprettOrganisasjon(@PathVariable("bestillingId") Long bestillingId,
                                                                  @RequestParam(value = "miljoer", required = false) String miljoer) {

        RsOrganisasjonBestillingStatus bestillingStatus = bestillingService.fetchBestillingStatusById(bestillingId);

        DeployRequest request = new DeployRequest(
                Set.of(bestillingStatus.getOrganisasjonNummer()),
                Set.of(miljoer.split(",")));

        RsOrganisasjonBestillingStatus status = RsOrganisasjonBestillingStatus.builder()
                .organisasjonNummer(request.getOrgnumre().iterator().next())
                .environments(request.getEnvironments())
                .bestilling(bestillingStatus.getBestilling())
                .build();

        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(status);

        organisasjonClient.gjenopprett(request, bestilling);

        return getStatus(bestilling, bestillingStatus.getOrganisasjonNummer());
    }

    @GetMapping()
    @Operation(description = "Hent opprettede organisasjoner basert på brukerId")
    public List<OrganisasjonDetaljer> hentOrganisasjoner(
            @Parameter(description = "BrukerID som er unik til en Azure bruker (Dolly autentisering)")
            @RequestParam(required = false) String brukerId) {

        return bestillingService.getOrganisasjoner(brukerId);
    }

}