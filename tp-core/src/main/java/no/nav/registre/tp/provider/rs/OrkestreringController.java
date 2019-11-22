package no.nav.registre.tp.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.tp.database.multitenancy.TenantContext;
import no.nav.registre.tp.provider.rs.request.OrkestreringRequest;
import no.nav.registre.tp.service.TpService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/orkestrering")
public class OrkestreringController {

    private final TpService tpService;

    @LogExceptions
    @ApiOperation(value = "Dette endepunktet kan benyttes for å initialisere en database i et gitt miljø. Identer som ikke finnes i TJPEN databasen, men i avspillergruppen på det gitte miljøet vil bli "
            + "opprettet uten noen tilhørende ytelser.")
    @PostMapping("/init")
    public ResponseEntity<Integer> initializeDatabase(
            @RequestBody OrkestreringRequest request
    ) {
        TenantContext.setTenant(request.getMiljoe());
        var count = tpService.initializeTpDbForEnvironment(request.getAvspillergruppeId());
        return ResponseEntity.ok(count);
    }

    @LogExceptions
    @ApiOperation(value = "Dette endepunktet kan benyttes for å opprette gitte personer. De vil bli opprettet i TJPEN. Det er ikke noen verifikasjon av FNR mot TPS eller om det er et gyldig FNR.")
    @PostMapping("/opprettPersoner/{miljoe}")
    public ResponseEntity<List<String>> addPeople(
            @RequestBody List<String> fnrs,
            @PathVariable String miljoe
    ) {
        TenantContext.setTenant(miljoe);
        var people = tpService.createPeople(fnrs);
        var feilet = fnrs.parallelStream().filter(fnr -> !people.contains(fnr)).collect(Collectors.toList());
        return ResponseEntity.ok(feilet);
    }
}
