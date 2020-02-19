package no.nav.registre.sdForvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.adapter.TpsAdapter;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.Team;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.Tps;
import no.nav.registre.sdForvalter.provider.rs.request.FastDataRequest;
import no.nav.registre.sdForvalter.service.StaticDataService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statiskData")
public class StaticDataController {

    private final StaticDataService staticDataService;
    private final TpsAdapter tpsAdapter;
    private final EregAdapter eregAdapter;

    @GetMapping("/team")
    public ResponseEntity<Set<Team>> getTeams() {
        return ResponseEntity.ok(staticDataService.getAllTeams());
    }

    @PostMapping("/team")
    public ResponseEntity<Team> saveTeam(@RequestBody Team team) {
        return ResponseEntity.ok(staticDataService.saveTeam(team));
    }

    @GetMapping("/team/{name}")
    public ResponseEntity<Team> getTeam(@PathVariable String name) {
        return ResponseEntity.ok(staticDataService.getTeam(name));
    }

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
        return ResponseEntity.ok(eregAdapter.fetchEregs());
    }

    @PostMapping(value = "/ereg")
    public ResponseEntity<List<Ereg>> createEregStaticData(@RequestBody List<Ereg> eregs) {
        return ResponseEntity.ok(eregAdapter.saveEregs(eregs));
    }

    @PostMapping(value = "/")
    public ResponseEntity<FastDataRequest> storeStaticDataInTps(@RequestBody FastDataRequest data) {
        FastDataRequest responseBody = new FastDataRequest();
        responseBody.setEier(data.getEier());
        responseBody.setTps(tpsAdapter.saveTps(data.getTps()));
        responseBody.setKrr(staticDataService.saveInKrr(data.getKrr(), data.getEier()));
        responseBody.setEreg(eregAdapter.saveEregs(data.getEreg()));
        responseBody.setAareg(staticDataService.saveInAareg(data.getAareg(), data.getEier()));
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping(value = "/ereg")
    public ResponseEntity<List<String>> deleteData(@RequestBody List<String> data) {
        return ResponseEntity.ok(staticDataService.deleteEreg(data));
    }

}
