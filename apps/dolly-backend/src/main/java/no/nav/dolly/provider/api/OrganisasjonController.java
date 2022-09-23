package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonClient;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.domain.MalbestillingNavn;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrganisasjonMalBestillingWrapper.RsOrganisasjonMalBestilling;
import no.nav.dolly.service.MalBestillingService;
import no.nav.dolly.service.OrganisasjonBestillingService;
import no.nav.dolly.service.OrganisasjonProgressService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/organisasjon")
public class OrganisasjonController {

    private final OrganisasjonClient organisasjonClient;
    private final OrganisasjonBestillingService bestillingService;
    private final MalBestillingService malBestillingService;
    private final OrganisasjonProgressService progressService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bestilling")
    @Operation(description = "Opprett organisasjon")
    public RsOrganisasjonBestillingStatus opprettOrganisasjonBestilling(@RequestBody RsOrganisasjonBestilling request) {

        OrganisasjonBestilling bestilling = bestillingService.saveBestilling(request);

        progressService.save(OrganisasjonBestillingProgress.builder()
                .bestilling(bestilling)
                .organisasjonsnummer("Ubestemt")
                .organisasjonsforvalterStatus(request.getEnvironments().stream().map(env -> env + ":Pågående").collect(Collectors.joining(",")))
                .build());

        organisasjonClient.opprett(request, bestilling);

        return getStatus(bestilling, "Ubestemt");
    }

    @PutMapping("/gjenopprett/{bestillingId}")
    @Operation(description = "Gjenopprett organisasjon")
    public RsOrganisasjonBestillingStatus gjenopprettOrganisasjon(@PathVariable("bestillingId") Long bestillingId,
                                                                  @RequestParam(value = "miljoer", required = false) String miljoer) {

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

        organisasjonClient.gjenopprett(request, bestilling);

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
            @Parameter(description = "BrukerID som er unik til en Azure bruker (Dolly autensiering)",
                    example = "1k9242uc-638g-1234-5678-7894k0j7lu6n") @RequestParam String brukerId) {

        return bestillingService.fetchBestillingStatusByBrukerId(brukerId);
    }

    @DeleteMapping("/bestilling/{orgnummer}")
    @Operation(description = "Slett gruppe")
    public void slettgruppe(@PathVariable("orgnummer") String orgnummer) {

        bestillingService.slettBestillingByOrgnummer(orgnummer);
    }

    @GetMapping("/malbestilling")
    @Operation(description = "Hent mal-bestilling")
    public RsOrganisasjonMalBestillingWrapper getMalBestillinger() {

        return malBestillingService.getOrganisasjonMalBestillinger();
    }

    @GetMapping("/malbestilling/bruker")
    @Operation(description = "Hent mal-bestillinger for en spesifikk bruker, kan filtreres på malnavn")
    public List<RsOrganisasjonMalBestilling> getMalbestillingByNavn(@RequestParam(value = "brukerId") String brukerId, @RequestParam(name = "malNavn", required = false) String malNavn) {

        return malBestillingService.getOrganisasjonMalbestillingByNavnAndUser(brukerId, malNavn);
    }

    @DeleteMapping("/malbestilling/{id}")
    @Operation(description = "Slett mal-bestilling")
    public void deleteMalBestilling(@PathVariable Long id) {

        bestillingService.redigerMalBestillingNavn(id, null);
    }

    @PutMapping("/malbestilling/{id}")
    @Operation(description = "Rediger mal-bestilling")
    public void redigerMalBestilling(@PathVariable Long id, @RequestBody MalbestillingNavn malbestillingNavn) {

        bestillingService.redigerMalBestillingNavn(id, malbestillingNavn.getMalNavn());
    }

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
                                .detaljert(Arrays.stream(bestilling.getMiljoer().split(","))
                                        .map(miljoe -> RsOrganisasjonStatusRapport.Detaljert.builder()
                                                .orgnummer(orgnummer)
                                                .miljo(miljoe)
                                                .build())
                                        .toList())
                                .build()))
                        .build()))
                .build();
    }
}