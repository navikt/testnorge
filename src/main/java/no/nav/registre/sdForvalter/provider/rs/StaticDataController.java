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

import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.KrrModel;
import no.nav.registre.sdForvalter.database.model.Team;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.provider.rs.request.FastDataRequest;
import no.nav.registre.sdForvalter.service.StaticDataService;

//TODO: Fix database update and create complex heriarcy
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statiskData")
public class StaticDataController {

    private final StaticDataService staticDataService;

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
    public ResponseEntity<Set<TpsModel>> getTpsStaticData() {
        return ResponseEntity.ok(staticDataService.getLocalTpsDatabaseData());
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
    public ResponseEntity<List<EregModel>> getEregStaticData() {
        return ResponseEntity.ok(staticDataService.getEregData());
    }


    @PostMapping(value = "/")
    public ResponseEntity<FastDataRequest> storeStaticDataInTps(@RequestBody FastDataRequest data) {
        FastDataRequest responseBody = new FastDataRequest();
        responseBody.setEier(data.getEier());
        responseBody.setTps(staticDataService.saveInTps(data.getTps(), data.getEier()));
        responseBody.setKrr(staticDataService.saveInKrr(data.getKrr(), data.getEier()));
        responseBody.setEreg(staticDataService.saveInEreg(data.getEreg(), data.getEier()));
        responseBody.setAareg(staticDataService.saveInAareg(data.getAareg(), data.getEier()));
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping(value = "/ereg")
    public ResponseEntity<List<String>> deleteData(@RequestBody List<String> data) {
        return ResponseEntity.ok(staticDataService.deleteEreg(data));
    }

}
