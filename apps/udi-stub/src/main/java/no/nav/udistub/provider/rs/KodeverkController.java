package no.nav.udistub.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.udistub.service.KodeverkService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import no.nav.udistub.database.model.Kodeverk;

@RestController
@RequestMapping("/api/v1/kodeverk")
@CacheConfig(cacheNames = "koder")
@RequiredArgsConstructor
public class KodeverkController {

    private final KodeverkService kodeverkService;

    @GetMapping("/{type}")
    public ResponseEntity<List<Kodeverk>> getKodeverkAvType(@PathVariable String type) {
        return ResponseEntity.ok(kodeverkService.finnAlleMedType(type));
    }

    @Cacheable(condition = "fom == null && tom == null")
    @GetMapping("/")
    public ResponseEntity<List<Kodeverk>> getKodeverk(@RequestParam(required = false) LocalDate tom, @RequestParam(required = false) LocalDate fom) {
        if (tom != null && fom != null)
            return ResponseEntity.ok(kodeverkService.finnAlleAktivMellom(fom, tom));
        else if (tom != null)
            return ResponseEntity.ok(kodeverkService.finnAlleAktivTom(tom));
        else if (fom != null)
            return ResponseEntity.ok(kodeverkService.finnAlleAktivFom(fom));
        else
            return ResponseEntity.ok(kodeverkService.finnAlle());
    }

    @Cacheable
    @GetMapping("/aktiv")
    public ResponseEntity<List<Kodeverk>> getAktiveKoder() {
        return ResponseEntity.ok(kodeverkService.finnAlleAktive());
    }

    @Cacheable
    @GetMapping("/inaktiv")
    public ResponseEntity<List<Kodeverk>> getInaktiveKoder() {
        return ResponseEntity.ok(kodeverkService.finnAlleInaktive());
    }

    @Cacheable
    @GetMapping("/{kode}")
    public ResponseEntity<Kodeverk> getKodeverk(@PathVariable String kode) {
        return ResponseEntity.ok(kodeverkService.finnMedKode(kode));
    }

}
