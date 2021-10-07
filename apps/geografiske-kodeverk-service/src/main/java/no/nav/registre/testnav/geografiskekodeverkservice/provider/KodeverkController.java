package no.nav.registre.testnav.geografiskekodeverkservice.provider;

import no.nav.registre.testnav.geografiskekodeverkservice.domain.Kodeverk;
import no.nav.registre.testnav.geografiskekodeverkservice.service.KodeverkService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
@Validated
public class KodeverkController {

    private final KodeverkService kodeverkService;
    private final CacheControl cacheControl;

    public KodeverkController(KodeverkService kodeverkService) {
        this.kodeverkService = kodeverkService;
        this.cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS).noTransform().mustRevalidate();
    }

    @GetMapping(value = "/kommuner")
    public ResponseEntity<List<Kodeverk>> getKommuner(
            @RequestParam(required = false) @Pattern(regexp = "[0-9]{4}", message = "Kommunenummer må være fire sifre") String kommunenr,
            @RequestParam(required = false) @Pattern(regexp = "\\D+", message = "Kommunenavn må ikke være alfanumerisk") String kommunenavn
    ) {
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(kodeverkService.getKommuner(kommunenr, kommunenavn));
    }

    @GetMapping(value = "/land")
    public ResponseEntity<List<Kodeverk>> getLand(
            @RequestParam(required = false) @Pattern(regexp = "[A-Z]{3}", message = "Landkode må være tre store bokstaver") String landkode,
            @RequestParam(required = false) @Pattern(regexp = "\\D+", message = "Land må ikke være alfanumerisk") String land
    ) {
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(kodeverkService.getLand(landkode, land));
    }

    @GetMapping(value = "/postnummer")
    public ResponseEntity<List<Kodeverk>> getPostnummer(
            @RequestParam(required = false) @Pattern(regexp = "[0-9]{4}", message = "Postnummer må være fire sifre") String postnummer,
            @RequestParam(required = false) @Pattern(regexp = "\\D+", message = "Poststed må ikke være alfanumerisk") String poststed
    ) {
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(kodeverkService.getPostnummer(postnummer, poststed));
    }

    @GetMapping(value = "/embeter")
    public ResponseEntity<List<Kodeverk>> getEmbeter(
            @RequestParam(required = false) @Pattern(regexp = "[A-Z]{4}", message = "Embetekode må være fire store bokstaver") String embetekode,
            @RequestParam(required = false) @Pattern(regexp = "\\D+", message = "Embetenavn må ikke være alfanumerisk") String embetenavn
    ) {
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(kodeverkService.getEmbeter(embetekode, embetenavn));

    }
}
