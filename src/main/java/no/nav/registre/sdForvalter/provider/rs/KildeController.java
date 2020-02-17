package no.nav.registre.sdForvalter.provider.rs;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdForvalter.adapter.KildeAdapter;
import no.nav.registre.sdForvalter.domain.Kilder;

@Api("Kildene til faste datasett")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kilde")
public class KildeController {

    private final KildeAdapter adapter;

    @GetMapping
    public ResponseEntity<Kilder> getKildeList() {
        return ResponseEntity.ok(adapter.getKilder());
    }
}
