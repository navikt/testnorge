package no.nav.registre.sdforvalter.provider.rs.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.status.ereg.OrganisasjonStatusMap;
import no.nav.registre.sdforvalter.service.EregStatusService;
import no.nav.testnav.libs.dto.statiskedataforvalter.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.statiskedataforvalter.v1.OrganisasjonListeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjons")
@Tag(
        name = "Organisasjoner",
        description = "Operasjoner på organisasjoner lagret i database."
)
public class OrganisasjonControllerV1 {

    private static final String ORGNR_REGEX = "^([89])\\d{8}$";

    private final EregAdapter eregAdapter;
    private final EregStatusService eregStatusService;

    @GetMapping("/status")
    @Operation(description = "Henter status for organisasjoner tabell EREG basert på tjenesten testnav-organisasjon-service, potensielt filtrert på gruppe.")
    public Mono<OrganisasjonStatusMap> statusByGruppe(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe", required = false) String gruppe
    ) {
        return Mono.fromCallable(() -> eregStatusService.getStatusByGruppe(miljo, gruppe, equal))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Validated
    @GetMapping("/status/{orgnr}")
    @Operation(description = "Henter status for organisasjoner tabell EREG basert på tjenesten testnav-organisasjon-service, potensielt filtrert på organisasjonsnummer.")
    public Mono<OrganisasjonStatusMap> statusByOrgnr(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @PathVariable @Pattern(regexp = ORGNR_REGEX) String orgnr) {
        return Mono.fromCallable(() -> eregStatusService.getStatusByOrgnr(miljo, orgnr, equal))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping
    @Operation(description = "Henter organisasjoner fra tabell EREG, potensielt filtrert på gruppe.")
    public Mono<OrganisasjonListeDTO> getOrganisasjons(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return Mono.fromCallable(() -> eregAdapter.fetchBy(gruppe).toDTO())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Validated
    @GetMapping("/{orgnr}")
    @Operation(description = "Henter organisasjon fra tabell EREG basert på organisasjonsnummer.")
    public Mono<OrganisasjonDTO> getOrganisasjon(@PathVariable @Pattern(regexp = ORGNR_REGEX) String orgnr) {
        return Mono.fromCallable(() -> Optional
                        .ofNullable(eregAdapter.fetchByOrgnr(orgnr))
                        .map(Ereg::toDTO)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Validated
    @PutMapping
    @Operation(description = "Lagrer en liste med organisasjoner i tabell EREG.")
    public Mono<OrganisasjonListeDTO> createOrganisasjons(@RequestBody OrganisasjonListeDTO listeDTO) {
        return Mono.fromCallable(() -> eregAdapter.save(new EregListe(listeDTO)).toDTO())
                .subscribeOn(Schedulers.boundedElastic());
    }
}