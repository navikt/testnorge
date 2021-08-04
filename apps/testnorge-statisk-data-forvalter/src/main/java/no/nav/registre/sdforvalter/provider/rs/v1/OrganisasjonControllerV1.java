package no.nav.registre.sdforvalter.provider.rs.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import javax.websocket.server.PathParam;

import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.status.ereg.OrganisasjonStatusMap;
import no.nav.registre.sdforvalter.service.EregStatusService;
import no.nav.testnav.libs.dto.statiskedataforvalter.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.statiskedataforvalter.v1.OrganisasjonListeDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjons")
public class OrganisasjonControllerV1 {

    private static final String ORGNR_REGEX = "^(8|9)\\d{8}$";

    private final EregAdapter eregAdapter;
    private final EregStatusService eregStatusService;

    @GetMapping("/status")
    public ResponseEntity<OrganisasjonStatusMap> statusByGruppe(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe", required = false) String gruppe
    ) {
        return ResponseEntity.ok(eregStatusService.getStatusByGruppe(miljo, gruppe, equal));
    }

    @Validated
    @GetMapping("/status/{orgnr}")
    public ResponseEntity<OrganisasjonStatusMap> statusByOrgnr(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @PathParam("orgnr") @Pattern(regexp = ORGNR_REGEX) String orgnr
    ) {
        return ResponseEntity.ok(eregStatusService.getStatusByOrgnr(miljo, orgnr, equal));
    }

    @GetMapping
    public ResponseEntity<OrganisasjonListeDTO> getOrganisasjons(@RequestParam(name = "gruppe", required = false) String gruppe) {
        OrganisasjonListeDTO dto = eregAdapter.fetchBy(gruppe).toDTO();
        return ResponseEntity.ok(dto);
    }

    @Validated
    @GetMapping("/{orgnr}")
    public ResponseEntity<OrganisasjonDTO> getOrganisasjon(@PathParam("orgnr") @Pattern(regexp = ORGNR_REGEX) String orgnr) {
        Ereg ereg = eregAdapter.fetchByOrgnr(orgnr);
        if (ereg == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ereg.toDTO());
    }

    @Validated
    @PutMapping
    public ResponseEntity<OrganisasjonListeDTO> createOrganisasjons(@RequestBody OrganisasjonListeDTO listeDTO) {
        OrganisasjonListeDTO dto = eregAdapter.save(new EregListe(listeDTO)).toDTO();
        return ResponseEntity.ok(dto);
    }
}