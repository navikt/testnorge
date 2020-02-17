package no.nav.registre.sdForvalter.provider.rs;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdForvalter.adapter.KildeSystemAdapter;
import no.nav.registre.sdForvalter.domain.KildeSystemListe;

@Api("Kilde systemet til de faste datasett")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kilde-system")
public class KildeSystemController {

    private final KildeSystemAdapter adapter;

    @GetMapping
    public ResponseEntity<KildeSystemListe> getKildeSystemList() {
        return ResponseEntity.ok(adapter.getKildeSystemListe());
    }
}
