package no.nav.testnav.apps.tpservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpservice.database.models.TForhold;
import no.nav.testnav.apps.tpservice.database.multitenancy.TenantContext;
import no.nav.testnav.apps.tpservice.provider.request.SyntetiseringsRequest;
import no.nav.testnav.apps.tpservice.service.SyntService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringsController {

    private final SyntService syntService;

    @Operation(description = "Dette endepunktet kan benyttes for å generere syntetiserte ytelser på tilfeldige personer i en gitt avspillergruppe som er definert i TPS-Forvalteren.")
    @PostMapping(value = "/generer")
    public ResponseEntity<Object> createYtelseWithRelations(
            @RequestBody @Valid SyntetiseringsRequest request
    ) {
        TenantContext.setTenant(request.getMiljoe());
        syntService.syntetiser(request);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Dette endepunktet kan benyttes for å hente ut alle forhold i et gitt miljø.")
    @GetMapping(value = "/forhold/{miljoe}")
    public List<TForhold> getForhold(
            @PathVariable String miljoe
    ) {
        TenantContext.setTenant(miljoe);
        return syntService.getForhold();
    }
}
