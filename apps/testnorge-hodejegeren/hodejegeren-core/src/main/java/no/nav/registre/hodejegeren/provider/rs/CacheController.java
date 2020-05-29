package no.nav.registre.hodejegeren.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.hodejegeren.service.CacheService;

@RestController
@RequestMapping("api/v1/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @GetMapping("/oppdaterGruppe/{avspillergruppeId}")
    @ApiOperation(value = "Her kan man manuelt trigge oppdatering av en cache med en gitt avspillergruppeId.")
    public String oppdaterGruppeCache(@PathVariable Long avspillergruppeId) {
        var oppdatertAvspillergruppe = cacheService.oppdaterAlleCacher(avspillergruppeId);
        return "Avspillergruppe " + oppdatertAvspillergruppe + " har oppdatert sine cacher.";
    }

    @GetMapping("/oppdaterAlle")
    @ApiOperation(value = "Her kan man manuelt trigge oppdatering av cachene til alle registrerte avspillergrupper. Endepunktet vil også fjerne "
            + "cacher til uregsitrerte avspillergrupper.")
    public String oppdaterAlleCaches() {
        var oppdaterteAvspillergrupper = cacheService.oppdaterAlleCacher();
        return "Avspillergrupper " + oppdaterteAvspillergrupper.toString() + " har oppdatert sine cacher";
    }

    @GetMapping("/grupper")
    @ApiOperation(value = "Her kan man hente id-ene til alle cachede avspillergrupper.")
    public Set<Long> hentCachedeAvspillergruppeIder() {
        return cacheService.hentCachedeAvspillergruppeIder();
    }
}
