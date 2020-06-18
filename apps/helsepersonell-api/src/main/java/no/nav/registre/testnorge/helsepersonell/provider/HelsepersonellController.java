package no.nav.registre.testnorge.helsepersonell.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeListeDTO;
import no.nav.registre.testnorge.helsepersonell.adapter.HelsepersonellAdapter;


@RestController
@RequestMapping("/api/v1/helsepersonell")
public class HelsepersonellController {

    private final HelsepersonellAdapter adapter;
    private final Integer legerCacheHours;


    public HelsepersonellController(
            HelsepersonellAdapter adapter,
            @Value("${helsepersonell.controller.leger.cache.hours}") Integer legerCacheHours
    ) {
        this.adapter = adapter;
        this.legerCacheHours = legerCacheHours;
    }

    @GetMapping("/leger")
    public ResponseEntity<LegeListeDTO> getLeger() {
        var cacheControl
                = CacheControl.maxAge(legerCacheHours, TimeUnit.HOURS)
                .noTransform()
                .mustRevalidate();
        var leger = adapter.getLeger();
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(leger.toDTO());
    }
}
