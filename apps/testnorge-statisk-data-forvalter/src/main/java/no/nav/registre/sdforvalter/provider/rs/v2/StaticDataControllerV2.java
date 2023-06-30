package no.nav.registre.sdforvalter.provider.rs.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.domain.EregListe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/faste-data")
@Tag(
        name = "StaticDataControllerV2",
        description = "Operasjoner på statiske EREG-data lagret i database."
)
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
    @Operation(description = "Henter fra tabell EREG.")
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
    @Operation(description = "Lagrer i tabell EREG.")
    public EregListe createEregStaticData(@RequestBody EregListe eregs) {
        return eregAdapter.save(eregs);
    }
}