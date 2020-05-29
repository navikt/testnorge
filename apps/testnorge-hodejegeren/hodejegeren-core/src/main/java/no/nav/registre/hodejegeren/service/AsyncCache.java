package no.nav.registre.hodejegeren.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasse som håndterer caching av avspillergrupper for å gjøre hodejegeren mer responsiv.
 * Avspillergrupper i listen fasteAvspillergrupper blir oppdatert regelmessig. Ved dette tidspunkt blir andre grupper slettet fra cachen.
 * Hodejegeren oppdaterer cachen på en gruppe etter at et kall har blitt gjort mot den gitte gruppen, for å holde cachen oppdatert til senere kall.
 */
@Slf4j
@Service
@Getter
@Setter
@RequiredArgsConstructor
@EnableScheduling
public class AsyncCache {

    private final TpsfFiltreringService tpsfFiltreringService;

    @Value("#{'${cache.avspillergrupper}'.split(',')}")
    private List<Long> fasteAvspillergrupper;

    private Map<Long, List<String>> alleIdenterCache;
    private Map<Long, List<String>> levendeIdenterCache;
    private Map<Long, List<String>> doedeOgUtvandredeIdenterCache;
    private Map<Long, List<String>> gifteIdenterCache;
    private Map<Long, List<String>> foedteIdenterCache;

    @Async
    public void asyncOppdaterAlleIdenterCache(Long avspillergruppeId) {
        oppdaterCache(alleIdenterCache, avspillergruppeId, tpsfFiltreringService.finnAlleIdenter(avspillergruppeId));
    }

    @Async
    public void asyncOppdaterLevendeIdenterCache(Long avspillergruppeId) {
        oppdaterCache(levendeIdenterCache, avspillergruppeId, tpsfFiltreringService.finnLevendeIdenter(avspillergruppeId));
    }

    @Async
    public void asyncOppdaterDoedeOgUtvandredeIdenterCache(Long avspillergruppeId) {
        oppdaterCache(doedeOgUtvandredeIdenterCache, avspillergruppeId, tpsfFiltreringService.finnDoedeOgUtvandredeIdenter(avspillergruppeId));
    }

    @Async
    public void asyncOppdaterGifteIdenterCache(Long avspillergruppeId) {
        oppdaterCache(gifteIdenterCache, avspillergruppeId, tpsfFiltreringService.finnGifteIdenter(avspillergruppeId));
    }

    @Async
    public void asyncOppdaterFoedteIdenterCache(Long avspillergruppeId) {
        oppdaterCache(foedteIdenterCache, avspillergruppeId, tpsfFiltreringService.finnFoedteIdenter(avspillergruppeId));
    }

    /**
     * Oppdaterer cachene én gang i timen. De avspillergruppene som ikke er registrert som faste cacher blir nullstilt ved dette tidspunkt
     */
    @Scheduled(cron = "0 0 * * * *")
    public void oppdaterAlleCacherRutine() {
        oppdaterAlleCacher();
    }

    public List<Long> oppdaterAlleCacher() {
        log.info("Oppdaterer cacher til avspillergrupper: {}", fasteAvspillergrupper.toString());
        sjekkOmCacherErNull();
        fjernUregistrerteCacher();

        for (Long avspillergruppeId : fasteAvspillergrupper) {
            oppdaterAlleIdenterCache(avspillergruppeId);
            oppdaterLevendeIdenterCache(avspillergruppeId);
            oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
            oppdaterGifteIdenterCache(avspillergruppeId);
            oppdaterFoedteIdenterCache(avspillergruppeId);
        }

        log.info("Faste cacher har blitt oppdatert");
        return fasteAvspillergrupper;
    }

    public Long oppdaterAlleCacher(Long avspillergruppeId) {
        log.info("Oppdaterer cacher til avspillergruppe: " + avspillergruppeId.toString().replaceAll("[\r\n]",""));
        oppdaterAlleIdenterCache(avspillergruppeId);
        oppdaterLevendeIdenterCache(avspillergruppeId);
        oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        oppdaterGifteIdenterCache(avspillergruppeId);
        return avspillergruppeId;
    }

    public void sjekkOmCacherErNull() {
        if (alleIdenterCache == null) {
            alleIdenterCache = new HashMap<>();
        }
        if (levendeIdenterCache == null) {
            levendeIdenterCache = new HashMap<>();
        }
        if (doedeOgUtvandredeIdenterCache == null) {
            doedeOgUtvandredeIdenterCache = new HashMap<>();
        }
        if (gifteIdenterCache == null) {
            gifteIdenterCache = new HashMap<>();
        }
        if (foedteIdenterCache == null) {
            foedteIdenterCache = new HashMap<>();
        }
    }

    public void fjernUregistrerteCacher() {
        fjernCache(alleIdenterCache);
        fjernCache(levendeIdenterCache);
        fjernCache(doedeOgUtvandredeIdenterCache);
        fjernCache(gifteIdenterCache);
        fjernCache(foedteIdenterCache);
    }

    public List<String> oppdaterAlleIdenterCache(Long avspillergruppeId) {
        if (alleIdenterCache == null) {
            alleIdenterCache = new HashMap<>();
        }
        return oppdaterCache(alleIdenterCache, avspillergruppeId, tpsfFiltreringService.finnAlleIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    public List<String> oppdaterLevendeIdenterCache(Long avspillergruppeId) {
        if (levendeIdenterCache == null) {
            levendeIdenterCache = new HashMap<>();
        }
        return oppdaterCache(levendeIdenterCache, avspillergruppeId, tpsfFiltreringService.finnLevendeIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    public List<String> oppdaterDoedeOgUtvandredeIdenterCache(Long avspillergruppeId) {
        if (doedeOgUtvandredeIdenterCache == null) {
            doedeOgUtvandredeIdenterCache = new HashMap<>();
        }
        return oppdaterCache(doedeOgUtvandredeIdenterCache, avspillergruppeId, tpsfFiltreringService.finnDoedeOgUtvandredeIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    public List<String> oppdaterGifteIdenterCache(Long avspillergruppeId) {
        if (gifteIdenterCache == null) {
            gifteIdenterCache = new HashMap<>();
        }
        return oppdaterCache(gifteIdenterCache, avspillergruppeId, tpsfFiltreringService.finnGifteIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    public List<String> oppdaterFoedteIdenterCache(Long avspillergruppeId) {
        if (foedteIdenterCache == null) {
            foedteIdenterCache = new HashMap<>();
        }
        return oppdaterCache(foedteIdenterCache, avspillergruppeId, tpsfFiltreringService.finnFoedteIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private void fjernCache(Map<Long, List<String>> cache) {
        for (var avspillergruppeId : cache.keySet()) {
            if (!fasteAvspillergrupper.contains(avspillergruppeId)) {
                cache.remove(avspillergruppeId);
            }
        }
    }

    private Map<Long, List<String>> oppdaterCache(Map<Long, List<String>> cache, Long avspillergruppeId, List<String> innhold) {
        if (cache.containsKey(avspillergruppeId)) {
            cache.get(avspillergruppeId).clear();
        }
        cache.put(avspillergruppeId, innhold);
        return cache;
    }
}
