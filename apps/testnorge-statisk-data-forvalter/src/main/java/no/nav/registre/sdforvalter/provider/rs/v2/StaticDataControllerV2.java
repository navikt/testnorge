package no.nav.registre.sdforvalter.provider.rs.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.domain.EregListe;

@Slf4j
@RestController
@RequestMapping("/api/v1/faste-data")
public class StaticDataControllerV2 {

    private final EregAdapter eregAdapter;
    private final Integer caching;

    public StaticDataControllerV2(
            EregAdapter eregAdapter,
            @Value("${controller.staticdata.cache.hours}") Integer caching
    ) {
        this.eregAdapter = eregAdapter;
        this.caching = caching;
    }

    @GetMapping("/ereg")
    public ResponseEntity<EregListe> getEregStaticData(@RequestParam(name = "gruppe", required = false) String gruppe) {
        var cacheControl
                = CacheControl.maxAge(caching, TimeUnit.HOURS)
                .noTransform()
                .mustRevalidate();

        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(eregAdapter.fetchBy(gruppe));
    }

    @PostMapping("/ereg")
    public ResponseEntity<EregListe> createEregStaticData(@RequestBody EregListe eregs) {
        return ResponseEntity.ok(eregAdapter.save(eregs));
    }
}