package no.nav.registre.medl.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.medl.adapter.AktoerAdapter;
import no.nav.registre.medl.consumer.rs.response.MedlSyntResponse;
import no.nav.registre.medl.database.model.TMedlemPeriode;
import no.nav.registre.medl.database.multitenancy.TenantContext;
import no.nav.registre.medl.service.SyntetiseringService;

@Slf4j
@RestController
@RequestMapping("api/v1/orkestrering")
@RequiredArgsConstructor
public class OrkestreringController {

    private final SyntetiseringService syntetiseringService;
    private final AktoerAdapter aktoerAdapter;

    @PostMapping("/medlemskap/{fnr}")
    public ResponseEntity<TMedlemPeriode> opprettMedlemskap(
            @PathVariable String fnr,
            @RequestParam(name = "miljoe") String miljoe,
            @RequestBody MedlSyntResponse body
    ) {
        TenantContext.setTenant(miljoe);
        var medlemPeriode = syntetiseringService.opprettDelvisMelding(body, fnr, miljoe);
        return ResponseEntity.ok(medlemPeriode);
    }

    @PostMapping("/filtrerIdenter")
    public List<String> filtrerIdenter(
            @RequestParam(name = "miljoe") String miljoe,
            @RequestBody List<String> identer
    ) {
        TenantContext.setTenant(miljoe);
        return aktoerAdapter.filtrerIdenter(identer);
    }
}
