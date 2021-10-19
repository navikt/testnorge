package no.nav.registre.sam.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.sam.multitenancy.TenantContext;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.sam.service.SyntetiseringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @ApiOperation(value = "Her kan man starte generering av syntetiske samordningsmeldinger på personer i en gitt TPSF-avspillergruppe i et gitt miljø.")
    @PostMapping(value = "/generer")
    public ResponseEntity<String> genererSamordningsmeldinger(
            @RequestBody SyntetiserSamRequest syntetiserSamRequest
    ) {
        TenantContext.setTenant(syntetiserSamRequest.getMiljoe());
        var identer = syntetiseringService.finnLevendeIdenter(syntetiserSamRequest);
        return syntetiseringService.opprettOgLagreSyntetiserteSamordningsmeldinger(identer);
    }
}