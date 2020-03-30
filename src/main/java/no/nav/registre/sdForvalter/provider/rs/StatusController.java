package no.nav.registre.sdForvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdForvalter.domain.status.ereg.OrganisasjonStatusMap;
import no.nav.registre.sdForvalter.service.EregStatusService;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
public class StatusController {

    private final EregStatusService eregStatusService;

    @GetMapping("/ereg")
    public ResponseEntity<OrganisasjonStatusMap> getEregStatus(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe", required = false) String gruppe
    ) {
        return ResponseEntity.ok(eregStatusService.getStatus(miljo, gruppe, equal));
    }
}
