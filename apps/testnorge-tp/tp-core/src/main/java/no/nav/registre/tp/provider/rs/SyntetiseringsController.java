package no.nav.registre.tp.provider.rs;

import io.swagger.annotations.ApiOperation;
import no.nav.registre.tp.database.multitenancy.TenantContext;
import no.nav.registre.tp.provider.rs.request.SyntetiseringsRequest;
import no.nav.registre.tp.service.TpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringsController {

    @Autowired
    private TpService tpService;

    @ApiOperation(value = "Dette endepunktet kan benyttes for å generere syntetiserte ytelser på tilfeldige personer i en gitt avspillergruppe som er definert i TPS-Forvalteren.")
    @PostMapping(value = "/generer")
    public ResponseEntity<Object> createYtelseWithRelations(
            @RequestBody @Valid SyntetiseringsRequest request
    ) {
        TenantContext.setTenant(request.getMiljoe());
        tpService.syntetiser(request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Dette endepunktet kan benyttes for å hente ut alle forhold i et gitt miljø.")
    @GetMapping(value = "/forhold/{miljoe}")
    public ResponseEntity<Object> getForhold(
            @PathVariable String miljoe
    ) {
        TenantContext.setTenant(miljoe);
        return ResponseEntity.ok(tpService.getForhold());
    }
}
