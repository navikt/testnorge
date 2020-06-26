package no.nav.registre.sdforvalter.provider.rs.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.domain.EregListe;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faste-data")
public class StaticDataControllerV2 {

    private final EregAdapter eregAdapter;

    @GetMapping("/ereg")
    public ResponseEntity<EregListe> getEregStaticData(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return ResponseEntity.ok(eregAdapter.fetchBy(gruppe));
    }

    @PostMapping("/ereg")
    public ResponseEntity<EregListe> createEregStaticData(@RequestBody EregListe eregs) {
        return ResponseEntity.ok(eregAdapter.save(eregs));
    }
}
