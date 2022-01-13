package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonClient;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.service.OrganisasjonBestillingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/organisasjon")
public class OrganisasjonController {

    private final OrganisasjonClient organisasjonClient;
    private final OrganisasjonBestillingService bestillingService;

    private static RsOrganisasjonBestillingStatus getStatus(OrganisasjonBestilling bestilling, String orgnummer) {

        return RsOrganisasjonBestillingStatus.builder()
                .id(bestilling.getId())
                .sistOppdatert(bestilling.getSistOppdatert())
                .antallLevert(0)
                .ferdig(false)
                .organisasjonNummer(orgnummer)
                .status(List.of(RsOrganisasjonStatusRapport.builder()
                        .id(SystemTyper.ORGANISASJON_FORVALTER)
                        .navn(SystemTyper.ORGANISASJON_FORVALTER.getBeskrivelse())
                        .statuser(List.of(RsOrganisasjonStatusRapport.Status.builder()
                                .melding("Bestilling startet ...")
                                .orgnummer(orgnummer)
                                .build()))
                        .build()))
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bestilling")
    @Operation(description = "Opprett organisasjon")
    public RsOrganisasjonBestillingStatus opprettOrganisasjonBestilling(@RequestBody RsOrganisasjonBestilling request) {

        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(request);
        organisasjonClient.opprett(request, bestilling.getId());

        return getStatus(bestilling, "Ubestemt");
    }

    @PutMapping("/gjenopprett/{bestillingId}")
    @Operation(description = "Gjenopprett organisasjon")
    public RsOrganisasjonBestillingStatus gjenopprettOrganisasjon(@PathVariable("bestillingId") Long bestillingId, @RequestParam(value = "miljoer", required = false) String miljoer) {

        RsOrganisasjonBestillingStatus bestillingStatus = bestillingService.fetchBestillingStatusById(bestillingId);

        DeployRequest request = new DeployRequest(
                Set.of(bestillingStatus.getOrganisasjonNummer()),
                asList(miljoer.split(",")));

        RsOrganisasjonBestillingStatus status = RsOrganisasjonBestillingStatus.builder()
                .organisasjonNummer(request.getOrgnumre().iterator().next())
                .environments(request.getEnvironments())
                .bestilling(bestillingStatus.getBestilling())
                .build();

        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(status);

        organisasjonClient.gjenopprett(request, bestilling.getId());

        return getStatus(bestilling, bestillingStatus.getOrganisasjonNummer());
    }

    @GetMapping("/bestilling")
    @Operation(description = "Hent status på bestilling basert på bestillingId")
    public RsOrganisasjonBestillingStatus hentBestilling(
            @Parameter(description = "ID på bestilling av organisasjon", example = "123") @RequestParam Long bestillingId) {

        return bestillingService.fetchBestillingStatusById(bestillingId);
    }

    @GetMapping("/bestillingsstatus")
    @Operation(description = "Hent status på bestilling basert på brukerId")
    public List<RsOrganisasjonBestillingStatus> hentBestillingStatus(
            @Parameter(description = "BrukerID som er unik til en Azure bruker (Dolly autensiering)", example = "1k9242uc-638g-1234-5678-7894k0j7lu6n") @RequestParam String brukerId) {

        return bestillingService.fetchBestillingStatusByBrukerId(brukerId);
    }

    @DeleteMapping("/bestilling/{orgnummer}")
    @Operation(description = "Slett gruppe")
    public void slettgruppe(@PathVariable("orgnummer") String orgnummer) {

        bestillingService.slettBestillingByOrgnummer(orgnummer);
    }
}