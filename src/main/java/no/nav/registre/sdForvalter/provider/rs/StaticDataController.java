package no.nav.registre.sdForvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.adapter.TpsAdapter;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.Tps;
import no.nav.registre.sdForvalter.provider.rs.request.FastDataRequest;
import no.nav.registre.sdForvalter.service.StaticDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statiskData")
public class StaticDataController {

    private final StaticDataService staticDataService;
    private final TpsAdapter tpsAdapter;
    private final EregAdapter eregAdapter;


    @GetMapping(value = "/tps")
    public ResponseEntity<Set<Tps>> getTpsStaticData() {
        return ResponseEntity.ok(tpsAdapter.fetchTps());
    }

    @PostMapping("/tps")
    public ResponseEntity<Set<Tps>> createTpsStaticData(@RequestBody Set<Tps> tpsSet) {
        return ResponseEntity.ok(tpsAdapter.saveTps(tpsSet));
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
    public ResponseEntity<List<Ereg>> getEregStaticData() {
        return ResponseEntity.ok(eregAdapter.fetchEregData());
    }

    @PostMapping(value = "/ereg")
    public ResponseEntity<List<Ereg>> createEregStaticData(@RequestBody List<Ereg> eregs) {
        return ResponseEntity.ok(eregAdapter.saveEregData(eregs));
    }

    @PostMapping(value = "/")
    public ResponseEntity<FastDataRequest> storeStaticDataInTps(@RequestBody FastDataRequest data) {
        FastDataRequest responseBody = new FastDataRequest();
        responseBody.setTps(tpsAdapter.saveTps(data.getTps()));
        responseBody.setKrr(staticDataService.saveInKrr(data.getKrr()));
        responseBody.setEreg(eregAdapter.saveEregData(data.getEreg()));
        responseBody.setAareg(staticDataService.saveInAareg(data.getAareg()));
        return ResponseEntity.ok(responseBody);
    }
}
