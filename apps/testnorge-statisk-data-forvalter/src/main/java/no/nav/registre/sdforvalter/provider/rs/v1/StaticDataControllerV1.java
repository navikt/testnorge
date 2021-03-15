package no.nav.registre.sdforvalter.provider.rs.v1;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdforvalter.adapter.AaregAdapter;
import no.nav.registre.sdforvalter.adapter.KrrAdapter;
import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.domain.AaregListe;
import no.nav.registre.sdforvalter.domain.KrrListe;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.registre.sdforvalter.service.IdentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faste-data")
public class StaticDataControllerV1 {

    private final IdentService identService;
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final AaregAdapter aaregAdapter;
    private final KrrAdapter krrAdapter;

    @GetMapping("/tps")
    public ResponseEntity<TpsIdentListe> getTps(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return ResponseEntity.ok(tpsIdenterAdapter.fetchBy(gruppe));
    }

    @PostMapping("/tps")
    public ResponseEntity<TpsIdentListe> createTps(@RequestBody TpsIdentListe liste,
                                                   @Parameter(description = "Hvis true settes tilfeldig navn på personer uten fornavn og etternavn")
                                                   @RequestParam(name = "genererManglendeNavn", required = false, defaultValue = "false") Boolean genererManglendeNavn) {
        return ResponseEntity.ok(identService.save(liste, genererManglendeNavn));
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
