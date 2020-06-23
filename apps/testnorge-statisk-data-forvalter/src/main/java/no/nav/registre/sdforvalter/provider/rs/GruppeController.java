package no.nav.registre.sdforvalter.provider.rs;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdforvalter.adapter.GruppeAdapter;
import no.nav.registre.sdforvalter.domain.GruppeListe;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/gruppe")
public class GruppeController {

    private final GruppeAdapter adapter;

    @GetMapping
    public ResponseEntity<GruppeListe> getGruppeListe() {
        return ResponseEntity.ok(adapter.fetchGruppeListe());
    }
}
