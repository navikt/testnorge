package no.nav.udistub.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.udistub.database.model.Kodeverk;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/kodeverk")
@CacheConfig(cacheNames = "koder")
@RequiredArgsConstructor
public class KodeverkController {

    private final KodeverkService kodeverkService;

    @GetMapping("/{type}")
    public ResponseEntity<List<Kodeverk>> getKodeverkAvType(@PathVariable String type) {
        log.info("Mottatt request getKodeverkAvType med type: {}", type);
        return ResponseEntity.ok(kodeverkService.finnAlleMedType(type));
    }

    @Cacheable(condition = "fom == null && tom == null")
    @GetMapping("/")
    public ResponseEntity<List<Kodeverk>> getKodeverk(@RequestParam(required = false) LocalDate tom, @RequestParam(required = false) LocalDate fom) {
        log.info("Mottatt request getKodeverk med tom: {}, fom: {}", tom, fom);
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
        log.info("Mottatt request getAktiveKoder");
        return ResponseEntity.ok(kodeverkService.finnAlleAktive());
    }

    @Cacheable
    @GetMapping("/inaktiv")
    public ResponseEntity<List<Kodeverk>> getInaktiveKoder() {
        log.info("Mottatt request getInaktiveKoder");
        return ResponseEntity.ok(kodeverkService.finnAlleInaktive());
    }

    @Cacheable
    @GetMapping("/{kode}")
    public ResponseEntity<Kodeverk> getKodeverk(@PathVariable String kode) {
        log.info("Mottatt request getKodeverk med kode: {}", kode);
        return ResponseEntity.ok(kodeverkService.finnMedKode(kode));
    }

}
