package no.nav.registre.sdForvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.service.EnvironmentInitializationService;
import no.nav.registre.sdForvalter.service.StaticDataService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/statiskData")
public class StaticDataController {

    private final EnvironmentInitializationService environmentInitializationService;
    private final StaticDataService staticDataService;

    @PostMapping(value = "/{miljoe}")
    public ResponseEntity initializeInEnvironment(@PathVariable String miljoe) {
        environmentInitializationService.initializeEnvironmentWithStaticData(miljoe);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/tps")
    public ResponseEntity<Set<TpsModel>> getTpsStaticData() {
        return ResponseEntity.ok(staticDataService.getLocalTpsDatabaseData());
    }

    @GetMapping(value = "/aareg")
    public ResponseEntity<Set<AaregModel>> getAaregStaticData() {
        return ResponseEntity.ok(staticDataService.getAaregData());
    }

    @GetMapping(value = "/dkif")
    public ResponseEntity<Set<KrrModel>> getDkifStaticData() {
        return ResponseEntity.ok(staticDataService.getDkifData());
    }

    @PostMapping(value = "/tps")
    public ResponseEntity storeStaticDataInTps(@Valid @RequestBody Set<TpsModel> data) {
        return ResponseEntity.ok(staticDataService.saveInTps(data));
    }

    @PostMapping(value = "/aargg")
    public ResponseEntity storeStaticDataInAaareg(@Valid @RequestBody Set<AaregModel> data) {
        return ResponseEntity.ok(staticDataService.saveInAareg(data));
    }

    @PostMapping(value = "/dkif")
    public ResponseEntity storeStaticData(@Valid @RequestBody Set<KrrModel> data) {
        return ResponseEntity.ok(staticDataService.saveInKrr(data));
    }

}
