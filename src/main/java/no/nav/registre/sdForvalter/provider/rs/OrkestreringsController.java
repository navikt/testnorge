package no.nav.registre.sdForvalter.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import no.nav.registre.sdForvalter.consumer.rs.response.AaregResponse;
import no.nav.registre.sdForvalter.service.EnvironmentInitializationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orkestrering")
public class OrkestreringsController {

    private final EnvironmentInitializationService environmentInitializationService;

    @ApiOperation(
            value = "Initialiserer alle dataene som er tilgjengelig i forvalteren sin database i et gitt miljø. Endepunktet spiller også av SKD-mantall gruppen og eventuelt legger til nye identer",
            consumes = "TPSF, Testnorge-SKD, Testnorge-aaregstub, Krr-stub, Testnorge-tp, Testnorge-SAM"
    )
    @PostMapping(value = "/{miljoe}")
    public ResponseEntity initializeInEnvironment(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) String gruppe) {
        environmentInitializationService.initializeEnvironmentWithStaticData(miljoe, gruppe);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Legger til nye identer fra faste data og spiller av til gitt miljø", consumes = "TPSF, Testnorge-SKD")
    @PostMapping(value = "/tps/{miljoe}")
    public ResponseEntity<Set<String>> initializeTps(@PathVariable String miljoe) {
        return ResponseEntity.ok(environmentInitializationService.initializeSkd(miljoe));
    }

    @ApiOperation(value = "Legger til arbeidsforhold i Aareg", consumes = "Testnorge-aareg")
    @PostMapping(value = "/aareg/{miljoe}")
    public List<AaregResponse> initializeAareg(@PathVariable String miljoe) {
        return environmentInitializationService.initializeAareg(miljoe);
    }

    @ApiOperation(value = "Legger til faste kontaktreservasjoner i krr-stub", consumes = "Krr-stub, Aktørregisteret")
    @PostMapping(value = "/krr")
    public ResponseEntity initializeKrr() {
        environmentInitializationService.initializeKrr();
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/ereg/{miljoe}")
    public ResponseEntity initializeEreg(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) String gruppe) {
        environmentInitializationService.initializeEreg(miljoe, gruppe);
        return ResponseEntity.ok().build();
    }
}
