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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public OrganisasjonStatusMap statusByGruppe(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe", required = false) String gruppe
    ) {
        return eregStatusService.getStatusByGruppe(miljo, gruppe, equal);
    }

    @Validated
    @GetMapping("/status/{orgnr}")
    @Operation(description = "Henter status for organisasjoner tabell EREG basert på tjenesten testnav-organisasjon-service, potensielt filtrert på organisasjonsnummer.")
    public OrganisasjonStatusMap statusByOrgnr(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @PathVariable @Pattern(regexp = ORGNR_REGEX) String orgnr) {
        return eregStatusService.getStatusByOrgnr(miljo, orgnr, equal);
    }

    @GetMapping
    @Operation(description = "Henter organisasjoner fra tabell EREG, potensielt filtrert på gruppe.")
    public OrganisasjonListeDTO getOrganisasjons(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return eregAdapter.fetchBy(gruppe).toDTO();
    }

    @Validated
    @GetMapping("/{orgnr}")
    @Operation(description = "Henter organisasjon fra tabell EREG basert på organisasjonsnummer.")
    public OrganisasjonDTO getOrganisasjon(@PathVariable @Pattern(regexp = ORGNR_REGEX) String orgnr) {
        return Optional
                .ofNullable(eregAdapter.fetchByOrgnr(orgnr))
                .map(Ereg::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Validated
    @PutMapping
    @Operation(description = "Lagrer en liste med organisasjoner i tabell EREG.")
    public OrganisasjonListeDTO createOrganisasjons(@RequestBody OrganisasjonListeDTO listeDTO) {
        return eregAdapter.save(new EregListe(listeDTO)).toDTO();
    }
}