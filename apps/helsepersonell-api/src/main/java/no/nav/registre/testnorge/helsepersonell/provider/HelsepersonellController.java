package no.nav.registre.testnorge.helsepersonell.provider;

import no.nav.registre.testnorge.helsepersonell.adapter.HelsepersonellAdapter;
import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/v1/helsepersonell")
public class HelsepersonellController {

    private final HelsepersonellAdapter adapter;
    private final Integer helsepersonellCacheHours;


    public HelsepersonellController(
            HelsepersonellAdapter adapter,
            @Value("${helsepersonell.controller.cache.hours}") Integer helsepersonellCacheHours
    ) {
        this.adapter = adapter;
        this.helsepersonellCacheHours = helsepersonellCacheHours;
    }

    @GetMapping
    public ResponseEntity<HelsepersonellListeDTO> getHelsepersonell() {
        var cacheControl
                = CacheControl.maxAge(helsepersonellCacheHours, TimeUnit.SECONDS)
                .noTransform()
                .mustRevalidate();
        var helsepersonell = adapter.getHelsepersonell();
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(helsepersonell.toDTO());
    }
}
