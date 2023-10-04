package no.nav.registre.tp.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.provider.request.OrkestreringRequest;
import no.nav.registre.tp.service.TpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/orkestrering")
public class OrkestreringController {

    private final TpService tpService;

    @Operation(description = "Dette endepunktet kan benyttes for å initialisere en database i et gitt miljø. Identer som ikke finnes i TJPEN databasen, men i avspillergruppen på det gitte miljøet vil bli "
            + "opprettet uten noen tilhørende ytelser.")
    @PostMapping("/init")
    public ResponseEntity<Integer> initializeDatabase(
            @RequestBody OrkestreringRequest request
    ) {
        var count = tpService.initializeTpDbForEnvironment(request.getAvspillergruppeId());
        return ResponseEntity.ok(count);
    }

    @Operation(description = "Dette endepunktet kan benyttes for å opprette gitte personer. De vil bli opprettet i TJPEN. Det er ikke noen verifikasjon av FNR mot TPS eller om det er et gyldig FNR.")
    @PostMapping("/opprettPersoner/{miljoe}")
    public ResponseEntity<List<String>> addPeople(
            @PathVariable String miljoe,
            @RequestBody List<String> fnrs
    ) {
        var people = tpService.createPeople(fnrs);
        var feilet = fnrs.parallelStream().filter(fnr -> !people.contains(fnr))
                .toList();
        return ResponseEntity.ok(feilet);
    }

    @Operation(description = "Dette endepunktet kan benyttes for å hente personer fra en gitt liste som finnes i TP.")
    @PostMapping("/hentPersonerITp/{miljoe}")
    public List<String> getPeopleInTp(
            @PathVariable String miljoe,
            @RequestBody List<String> fnrs
    ) {
        return tpService.filterTpOnFnrs(fnrs);
    }

    @Operation(description = "Enkel implementasjon for å fjerne personer i en gitt liste fra TP. Personene fjernes kun hvis de ikke"
            + "har tilhørende forhold. Returnerer en liste med de personene som ble slettet.")
    @DeleteMapping("/fjernPersoner/{miljoe}")
    public List<String> removeIdentsFromTp(@PathVariable String miljoe, @RequestBody List<String> fnrs) {
        return tpService.removeFnrsFromTp(fnrs);
    }
}
