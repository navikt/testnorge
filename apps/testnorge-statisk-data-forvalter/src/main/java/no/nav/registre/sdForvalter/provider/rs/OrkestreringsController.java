package no.nav.registre.sdForvalter.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdForvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdForvalter.consumer.rs.PersonConsumer;
import no.nav.registre.sdForvalter.domain.TpsIdentListe;
import no.nav.registre.sdForvalter.service.EnvironmentInitializationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orkestrering")
public class OrkestreringsController {

    private final EnvironmentInitializationService environmentInitializationService;
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final PersonConsumer personConsumer;

    @ApiOperation(
            value = "Initialiserer alle dataene som er tilgjengelig i forvalteren sin database i et gitt miljø. Endepunktet spiller også av SKD-mantall gruppen og eventuelt legger til nye identer",
            consumes = "TPSF, Testnorge-SKD, Testnorge-aaregstub, Krr-stub, Testnorge-tp, Testnorge-SAM"
    )
    @PostMapping(value = "/{miljoe}")
    public ResponseEntity<HttpStatus> initializeInEnvironment(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) String gruppe) {
        environmentInitializationService.initializeEnvironmentWithStaticData(miljoe, gruppe);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Legger til nye identer fra faste data og spiller av til gitt miljø", consumes = "TPSF, Testnorge-SKD")
    @PostMapping(value = "/tps/{miljoe}")
    public ResponseEntity<HttpStatus> initializeTps(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) String gruppe) {
        environmentInitializationService.initializeIdent(miljoe, gruppe);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/pdl/{miljoe}")
    public ResponseEntity<HttpStatus> initializePdlGruppe(@RequestParam(name = "gruppe") String gruppe) {
        TpsIdentListe liste = tpsIdenterAdapter.fetchBy(gruppe);
        personConsumer.send(liste);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Legger til arbeidsforhold i Aareg", consumes = "Testnorge-aareg")
    @PostMapping(value = "/aareg/{miljoe}")
    public ResponseEntity<HttpStatus> initializeAareg(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) String gruppe) {
        environmentInitializationService.initializeAareg(miljoe, gruppe);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Legger til faste kontaktreservasjoner i krr-stub", consumes = "Krr-stub, Aktørregisteret")
    @PostMapping(value = "/krr")
    public ResponseEntity<HttpStatus> initializeKrr(@RequestParam(name = "gruppe", required = false) String gruppe) {
        environmentInitializationService.initializeKrr(gruppe);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/ereg/{miljoe}")
    public ResponseEntity<HttpStatus> initializeEreg(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) String gruppe) {
        environmentInitializationService.initializeEreg(miljoe, gruppe);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/ereg/{miljoe}/update/{regnr}")
    public ResponseEntity<HttpStatus> updateEreg(@PathVariable String miljoe, @PathVariable("regnr") String regnr) {
        environmentInitializationService.updateEregByOrgnr(miljoe, regnr);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/ereg/{miljoe}/update")
    public ResponseEntity<HttpStatus> updateEregByGruppe(@PathVariable String miljoe, @RequestParam(name = "gruppe") String gruppe) {
        environmentInitializationService.updateEregByGruppe(miljoe, gruppe);
        return ResponseEntity.ok().build();
    }
}
