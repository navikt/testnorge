package no.nav.registre.sdForvalter.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;
import no.nav.registre.sdForvalter.domain.TpsIdent;
import no.nav.registre.sdForvalter.service.StaticDataService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faste-data")
public class StaticDataController {

    private final StaticDataService staticDataService;
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final EregAdapter eregAdapter;


    @GetMapping(value = "/tps")
    public ResponseEntity<Set<TpsIdent>> getTpsStaticData() {
        return ResponseEntity.ok(tpsIdenterAdapter.fetchTpsIdenter());
    }

    @PostMapping("/tps")
    public ResponseEntity<Set<TpsIdent>> createTpsStaticData(@RequestBody Set<TpsIdent> tpsIdentSet) {
        return ResponseEntity.ok(tpsIdenterAdapter.saveTpsIdenter(tpsIdentSet));
    }

    @GetMapping(value = "/aareg")
    public ResponseEntity<Set<AaregModel>> getAaregStaticData() {
        return ResponseEntity.ok(staticDataService.getAaregData());
    }

    @GetMapping(value = "/krr")
    public ResponseEntity<Set<KrrModel>> getDkifStaticData() {
        return ResponseEntity.ok(staticDataService.getDkifData());
    }

    @GetMapping(value = "/ereg")
    public ResponseEntity<EregListe> getEregStaticData(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return ResponseEntity.ok(eregAdapter.fetchEregData(gruppe));
    }

    @PostMapping(value = "/ereg")
    public ResponseEntity<EregListe> createEregStaticData(@RequestBody EregListe eregs) {
        return ResponseEntity.ok(eregAdapter.saveEregData(eregs));
    }
}
