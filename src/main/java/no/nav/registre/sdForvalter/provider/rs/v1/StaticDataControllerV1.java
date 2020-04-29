package no.nav.registre.sdForvalter.provider.rs.v1;

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
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.adapter.AaregAdapter;
import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.adapter.KrrAdapter;
import no.nav.registre.sdForvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdForvalter.domain.AaregListe;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;
import no.nav.registre.sdForvalter.domain.KrrListe;
import no.nav.registre.sdForvalter.domain.TpsIdentListe;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faste-data")
public class StaticDataControllerV1 {

    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final EregAdapter eregAdapter;
    private final AaregAdapter aaregAdapter;
    private final KrrAdapter krrAdapter;


    @GetMapping("/tps")
    public ResponseEntity<TpsIdentListe> getTps(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return ResponseEntity.ok(tpsIdenterAdapter.fetchBy(gruppe));
    }

    @PostMapping("/tps")
    public ResponseEntity<TpsIdentListe> createTps(@RequestBody TpsIdentListe liste) {
        return ResponseEntity.ok(tpsIdenterAdapter.save(liste));
    }

    @GetMapping("/aareg")
    public ResponseEntity<AaregListe> getAareg(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return ResponseEntity.ok(aaregAdapter.fetchBy(gruppe));
    }

    @PostMapping("/aareg")
    public ResponseEntity<AaregListe> createAareg(@RequestBody AaregListe liste) {
        return ResponseEntity.ok(aaregAdapter.save(liste));
    }

    @GetMapping("/krr")
    public ResponseEntity<KrrListe> getKrr(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return ResponseEntity.ok(krrAdapter.fetchBy(gruppe));
    }

    @PostMapping("/krr")
    public ResponseEntity<KrrListe> createKrr(@RequestBody KrrListe liste) {
        return ResponseEntity.ok(krrAdapter.save(liste));
    }

}
