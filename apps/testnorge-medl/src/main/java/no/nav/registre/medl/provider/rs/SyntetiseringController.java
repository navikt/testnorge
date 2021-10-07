package no.nav.registre.medl.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.medl.database.model.TMedlemPeriode;
import no.nav.registre.medl.database.multitenancy.TenantContext;
import no.nav.registre.medl.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.medl.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    public ResponseEntity<List<TMedlemPeriode>> genererMedlemskapsmeldinger(
            @RequestBody SyntetiserMedlRequest syntetiserMedlRequest
    ) {
        TenantContext.setTenant(syntetiserMedlRequest.getMiljoe());
        return ResponseEntity.ok(syntetiseringService.opprettMeldinger(syntetiserMedlRequest));
    }
}