package no.nav.registre.hodejegeren.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.hodejegeren.service.CacheService;

@RestController
@RequestMapping("api/v1/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @GetMapping("/oppdaterGruppe/{avspillergruppeId}")
    public String oppdaterGruppeCache(@PathVariable Long avspillergruppeId) {
        Long oppdatertAvspillergruppe = cacheService.oppdaterAlleCacher(avspillergruppeId);
        return "Avspillergruppe " + oppdatertAvspillergruppe + " har oppdatert sine cacher.";
    }

    @GetMapping("/oppdaterAlle")
    public String oppdaterAlleCaches() {
        List<Long> oppdaterteAvspillergrupper = cacheService.oppdaterAlleCacher();
        return "Avspillergrupper " + oppdaterteAvspillergrupper.toString() + " har oppdatert sine cacher";
    }
}
