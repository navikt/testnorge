package no.nav.registre.hodejegeren.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
@EnableScheduling
public class CacheService {

    private final TpsfFiltreringService tpsfFiltreringService;

    @Value("#{'${cache.avspillergrupper}'.split(',')}")
    private List<Long> fasteAvspillergrupper;

    private Map<Long, List<String>> alleIdenterCache;
    private Map<Long, List<String>> levendeIdenterCache;
    private Map<Long, List<String>> doedeOgUtvandredeIdenterCache;
    private Map<Long, List<String>> gifteIdenterCache;
    private Map<Long, List<String>> foedteIdenterCache;

    public List<String> hentAlleIdenterCache(Long avspillergruppeId) {
        List<String> alleIdenter;
        if (alleIdenterCache == null || !alleIdenterCache.containsKey(avspillergruppeId) || alleIdenterCache.get(avspillergruppeId).isEmpty()) {
            alleIdenter = oppdaterAlleIdenterCache(avspillergruppeId);
        } else {
            alleIdenter = alleIdenterCache.get(avspillergruppeId);
            oppdaterAlleIdenterCache(avspillergruppeId);
        }
        return alleIdenter;
    }

    public List<String> hentLevendeIdenterCache(Long avspillergruppeId) {
        List<String> levendeIdenter;
        if (levendeIdenterCache == null || !levendeIdenterCache.containsKey(avspillergruppeId) || levendeIdenterCache.get(avspillergruppeId).isEmpty()) {
            levendeIdenter = oppdaterLevendeIdenterCache(avspillergruppeId);
        } else {
            levendeIdenter = levendeIdenterCache.get(avspillergruppeId);
            oppdaterLevendeIdenterCache(avspillergruppeId);
        }
        return levendeIdenter;
    }

    public List<String> hentDoedeOgUtvandredeIdenterCache(Long avspillergruppeId) {
        List<String> doedeOgUtvandredeIdenter;
        if (doedeOgUtvandredeIdenterCache == null || !doedeOgUtvandredeIdenterCache.containsKey(avspillergruppeId) || doedeOgUtvandredeIdenterCache.get(avspillergruppeId).isEmpty()) {
            doedeOgUtvandredeIdenter = oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        } else {
            doedeOgUtvandredeIdenter = doedeOgUtvandredeIdenterCache.get(avspillergruppeId);
            oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        }
        return doedeOgUtvandredeIdenter;
    }

    public List<String> hentGifteIdenterCache(Long avspillergruppeId) {
        List<String> gifteIdenter;
        if (gifteIdenterCache == null || !gifteIdenterCache.containsKey(avspillergruppeId) || gifteIdenterCache.get(avspillergruppeId).isEmpty()) {
            gifteIdenter = oppdaterGifteIdenterCache(avspillergruppeId);
        } else {
            gifteIdenter = gifteIdenterCache.get(avspillergruppeId);
            oppdaterGifteIdenterCache(avspillergruppeId);
        }
        return gifteIdenter;
    }

    public List<String> hentFoedteIdenterCache(Long avspillergruppeId) {
        List<String> foedteIdenter;
        if (foedteIdenterCache == null || !foedteIdenterCache.containsKey(avspillergruppeId) || foedteIdenterCache.get(avspillergruppeId).isEmpty()) {
            foedteIdenter = oppdaterFoedteIdenterCache(avspillergruppeId);
        } else {
            foedteIdenter = foedteIdenterCache.get(avspillergruppeId);
            oppdaterFoedteIdenterCache(avspillergruppeId);
        }
        return foedteIdenter;
    }

    /**
     * Oppdaterer cachene en gang i timen. De avspillergruppene som ikke er registrert som faste cacher blir nullstilt ved dette tidspunkt
     */
    @Scheduled(cron = "0 0 * * * *")
    public List<Long> oppdaterAlleCacher() {
        log.info("Oppdaterer cacher til avspillergrupper: {}", fasteAvspillergrupper.toString());

        for (Long avspillergruppeId : alleIdenterCache.keySet()) {
            if (!fasteAvspillergrupper.contains(avspillergruppeId)) {
                alleIdenterCache.remove(avspillergruppeId);
            } else {
                oppdaterAlleIdenterCache(avspillergruppeId);
            }
        }

        for (Long avspillergruppeId : levendeIdenterCache.keySet()) {
            if (!fasteAvspillergrupper.contains(avspillergruppeId)) {
                levendeIdenterCache.remove(avspillergruppeId);
            } else {
                oppdaterLevendeIdenterCache(avspillergruppeId);
            }
        }

        for (Long avspillergruppeId : doedeOgUtvandredeIdenterCache.keySet()) {
            if (!fasteAvspillergrupper.contains(avspillergruppeId)) {
                doedeOgUtvandredeIdenterCache.remove(avspillergruppeId);
            } else {
                oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
            }
        }

        for (Long avspillergruppeId : gifteIdenterCache.keySet()) {
            if (!fasteAvspillergrupper.contains(avspillergruppeId)) {
                gifteIdenterCache.remove(avspillergruppeId);
            } else {
                oppdaterGifteIdenterCache(avspillergruppeId);
            }
        }

        return fasteAvspillergrupper;
    }

    public Long oppdaterAlleCacher(Long avspillergruppeId) {
        log.info("Oppdaterer cacher til avspillergruppe: {}", avspillergruppeId);
        oppdaterAlleIdenterCache(avspillergruppeId);
        oppdaterLevendeIdenterCache(avspillergruppeId);
        oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        oppdaterGifteIdenterCache(avspillergruppeId);
        return avspillergruppeId;
    }

    private List<String> oppdaterAlleIdenterCache(Long avspillergruppeId) {
        if (alleIdenterCache == null) {
            alleIdenterCache = new HashMap<>();
        }
        return oppdaterCache(alleIdenterCache, avspillergruppeId, tpsfFiltreringService.finnAlleIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private List<String> oppdaterLevendeIdenterCache(Long avspillergruppeId) {
        if (levendeIdenterCache == null) {
            levendeIdenterCache = new HashMap<>();
        }
        return oppdaterCache(levendeIdenterCache, avspillergruppeId, tpsfFiltreringService.finnLevendeIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private List<String> oppdaterDoedeOgUtvandredeIdenterCache(Long avspillergruppeId) {
        if (doedeOgUtvandredeIdenterCache == null) {
            doedeOgUtvandredeIdenterCache = new HashMap<>();
        }
        return oppdaterCache(doedeOgUtvandredeIdenterCache, avspillergruppeId, tpsfFiltreringService.finnDoedeOgUtvandredeIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private List<String> oppdaterGifteIdenterCache(Long avspillergruppeId) {
        if (gifteIdenterCache == null) {
            gifteIdenterCache = new HashMap<>();
        }
        return oppdaterCache(gifteIdenterCache, avspillergruppeId, tpsfFiltreringService.finnGifteIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private List<String> oppdaterFoedteIdenterCache(Long avspillergruppeId) {
        if (foedteIdenterCache == null) {
            foedteIdenterCache = new HashMap<>();
        }
        return oppdaterCache(foedteIdenterCache, avspillergruppeId, tpsfFiltreringService.finnFoedteIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private Map<Long, List<String>> oppdaterCache(Map<Long, List<String>> cache, Long avspillergruppeId, List<String> innhold) {
        if (cache.containsKey(avspillergruppeId)) {
            cache.get(avspillergruppeId).clear();
        }
        cache.put(avspillergruppeId, innhold);
        return cache;
    }
}
