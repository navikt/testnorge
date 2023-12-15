package no.nav.registre.sdforvalter.provider.rs.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.adapter.AaregAdapter;
import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.adapter.KrrAdapter;
import no.nav.registre.sdforvalter.domain.AaregListe;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.KrrListe;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faste-data")
@Tag(
        name = "StaticDataControllerV1",
        description = "Operasjoner på statiske AAREG-/KRR- og EREG-data lagret i database."
)
public class StaticDataControllerV1 {

    private static final int CACHE_HOURS = 24;

    private final AaregAdapter aaregAdapter;
    private final KrrAdapter krrAdapter;
    private final EregAdapter eregAdapter;

    @GetMapping("/aareg")
    @Operation(description = "Henter AAREG-data fra tabell AAREG.")
    public AaregListe getAareg(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return aaregAdapter.fetchBy(gruppe);
    }

    @PostMapping("/aareg")
    @Operation(description = "Lagrer AAREG-data i tabell AAREG.")
    public AaregListe createAareg(@RequestBody AaregListe liste) {
        return aaregAdapter.save(liste);
    }

    @GetMapping("/krr")
    @Operation(description = "Henter KRR-data fra tabell KRR.")
    public KrrListe getKrr(@RequestParam(name = "gruppe", required = false) String gruppe) {
        return krrAdapter.fetchBy(gruppe);
    }

    @PostMapping("/krr")
    @Operation(description = "Lagrer KRR-data i tabell KRR.")
    public KrrListe createKrr(@RequestBody KrrListe liste) {
        return krrAdapter.save(liste);
    }

    @GetMapping("/ereg")
    @Operation(description = "Henter fra tabell EREG.")
    public ResponseEntity<EregListe> getEregStaticData(@RequestParam(name = "gruppe", required = false) String gruppe) {
        var cacheControl = CacheControl
                .maxAge(CACHE_HOURS, TimeUnit.HOURS)
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
