package no.nav.registre.sdForvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.TpsIdent;
import no.nav.registre.sdForvalter.provider.rs.request.FastDataRequest;
import no.nav.registre.sdForvalter.service.StaticDataService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statiskData")
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
        responseBody.setTps(tpsIdenterAdapter.saveTpsIdenter(data.getTps()));
        responseBody.setKrr(staticDataService.saveInKrr(data.getKrr()));
        responseBody.setEreg(eregAdapter.saveEregData(data.getEreg()));
        responseBody.setAareg(staticDataService.saveInAareg(data.getAareg()));
        return ResponseEntity.ok(responseBody);
    }
}
