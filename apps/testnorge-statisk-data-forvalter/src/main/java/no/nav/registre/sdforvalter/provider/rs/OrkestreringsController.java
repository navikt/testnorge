package no.nav.registre.sdforvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.consumer.rs.person.PersonConsumer;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.registre.sdforvalter.provider.rs.dto.Orgnummer;
import no.nav.registre.sdforvalter.service.EnvironmentInitializationService;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orkestrering")
public class OrkestreringsController {

    private final EnvironmentInitializationService environmentInitializationService;
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final PersonConsumer personConsumer;

    @PostMapping(value = "/{miljoe}")
    public ResponseEntity<HttpStatus> initializeInEnvironment(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeEnvironmentWithStaticData(miljoe, gruppe.name());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/tps/{miljoe}")
    public ResponseEntity<HttpStatus> initializeTps(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeIdent(miljoe, gruppe.name());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/pdl")
    public ResponseEntity<HttpStatus> initializePdlGruppe(@RequestParam(name = "gruppe") Gruppe gruppe) {
        TpsIdentListe liste = tpsIdenterAdapter.fetchBy(gruppe.name());
        personConsumer.opprettPersoner(liste);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/aareg/{miljoe}")
    public ResponseEntity<HttpStatus> initializeAareg(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeAareg(miljoe, gruppe.name());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/krr")
    public ResponseEntity<HttpStatus> initializeKrr(@RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeKrr(gruppe.name());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/ereg/{miljoe}")
    public ResponseEntity<HttpStatus> initializeEreg(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeEreg(miljoe, gruppe.name());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/ereg/{miljoe}/update/{regnr}")
    public ResponseEntity<HttpStatus> updateEreg(@PathVariable String miljoe, @PathVariable("regnr") String regnr) {
        environmentInitializationService.updateEregByOrgnr(miljoe, regnr);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/ereg/{miljoe}/update")
    public ResponseEntity<HttpStatus> updateEregByGruppe(@PathVariable String miljoe, @RequestParam(name = "gruppe") Gruppe gruppe) {
        environmentInitializationService.updateEregByGruppe(miljoe, gruppe.name());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/organisasjoner")
    public ResponseEntity<HttpStatus> saveBy(@RequestParam String miljoe, @RequestBody Orgnummer orgnummer) {
        environmentInitializationService.opprett(miljoe, orgnummer.getListe());
        return ResponseEntity.ok().build();
    }

}
