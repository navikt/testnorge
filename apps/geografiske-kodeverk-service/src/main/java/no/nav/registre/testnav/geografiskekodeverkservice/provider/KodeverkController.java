package no.nav.registre.testnav.geografiskekodeverkservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnav.geografiskekodeverkservice.domain.Kodeverk;
import no.nav.registre.testnav.geografiskekodeverkservice.service.KodeverkService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")

public class KodeverkController {

    private final KodeverkService kodeverkService;
    private final CacheControl cacheControl;

    public KodeverkController(KodeverkService kodeverkService) {
        this.kodeverkService = kodeverkService;
        this.cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS).noTransform().mustRevalidate();
    }

    @GetMapping(value = "/kommuner")
    public ResponseEntity<List<Kodeverk>> getKommuner(@RequestParam(required = false) String kommunenr) {
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(kodeverkService.getKommuner(kommunenr));
    }

    @GetMapping(value = "/land")
    public ResponseEntity<List<Kodeverk>> getLand(@RequestParam(required = false) String landkode) {
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(kodeverkService.getLand(landkode));
    }

    @GetMapping(value = "/postnummer")
    public ResponseEntity<List<Kodeverk>> getPostnummer(@RequestParam(required = false) String poststed) {
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(kodeverkService.getPostnummer(poststed));

    }

    @GetMapping(value = "/embeter")
    public ResponseEntity<List<Kodeverk>> getEmbeter() {
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(kodeverkService.getEmbeter());

    }
}
