package no.nav.registre.hodejegeren.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@EnableScheduling
public class CacheService {

    private final TpsfFiltreringService tpsfFiltreringService;

    private final AsyncCache asyncCache;

    @Value("#{'${cache.avspillergrupper}'.split(',')}")
    private List<Long> fasteAvspillergrupper;

    public List<String> hentAlleIdenterCache(Long avspillergruppeId) {
        List<String> alleIdenter;
        Map<Long, List<String>> alleIdenterCache = asyncCache.getAlleIdenterCache();
        if (alleIdenterCache == null || !alleIdenterCache.containsKey(avspillergruppeId) || alleIdenterCache.get(avspillergruppeId).isEmpty()) {
            alleIdenter = oppdaterAlleIdenterCache(avspillergruppeId);
        } else {
            alleIdenter = alleIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterAlleIdenterCache(avspillergruppeId);
        }
        return alleIdenter;
    }

    public List<String> hentLevendeIdenterCache(Long avspillergruppeId) {
        List<String> levendeIdenter;
        Map<Long, List<String>> levendeIdenterCache = asyncCache.getLevendeIdenterCache();
        if (levendeIdenterCache == null || !levendeIdenterCache.containsKey(avspillergruppeId) || levendeIdenterCache.get(avspillergruppeId).isEmpty()) {
            levendeIdenter = oppdaterLevendeIdenterCache(avspillergruppeId);
        } else {
            levendeIdenter = levendeIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterLevendeIdenterCache(avspillergruppeId);
        }
        return levendeIdenter;
    }

    public List<String> hentDoedeOgUtvandredeIdenterCache(Long avspillergruppeId) {
        List<String> doedeOgUtvandredeIdenter;
        Map<Long, List<String>> doedeOgUtvandredeIdenterCache = asyncCache.getDoedeOgUtvandredeIdenterCache();
        if (doedeOgUtvandredeIdenterCache == null || !doedeOgUtvandredeIdenterCache.containsKey(avspillergruppeId) || doedeOgUtvandredeIdenterCache.get(avspillergruppeId).isEmpty()) {
            doedeOgUtvandredeIdenter = oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        } else {
            doedeOgUtvandredeIdenter = doedeOgUtvandredeIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        }
        return doedeOgUtvandredeIdenter;
    }

    public List<String> hentGifteIdenterCache(Long avspillergruppeId) {
        List<String> gifteIdenter;
        Map<Long, List<String>> gifteIdenterCache = asyncCache.getGifteIdenterCache();
        if (gifteIdenterCache == null || !gifteIdenterCache.containsKey(avspillergruppeId) || gifteIdenterCache.get(avspillergruppeId).isEmpty()) {
            gifteIdenter = oppdaterGifteIdenterCache(avspillergruppeId);
        } else {
            gifteIdenter = gifteIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterGifteIdenterCache(avspillergruppeId);
        }
        return gifteIdenter;
    }

    public List<String> hentFoedteIdenterCache(Long avspillergruppeId) {
        List<String> foedteIdenter;
        Map<Long, List<String>> foedteIdenterCache = asyncCache.getFoedteIdenterCache();
        if (foedteIdenterCache == null || !foedteIdenterCache.containsKey(avspillergruppeId) || foedteIdenterCache.get(avspillergruppeId).isEmpty()) {
            foedteIdenter = oppdaterFoedteIdenterCache(avspillergruppeId);
        } else {
            foedteIdenter = foedteIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterFoedteIdenterCache(avspillergruppeId);
        }
        return foedteIdenter;
    }

    /**
     * Oppdaterer cachene en gang i timen. De avspillergruppene som ikke er registrert som faste cacher blir nullstilt ved dette tidspunkt
     */
    @Scheduled(cron = "0 0 * * * *")
    public List<Long> oppdaterAlleCacher() {
        log.info("Oppdaterer cacher til avspillergrupper: {}", fasteAvspillergrupper.toString());
        asyncCache.sjekkOmCacherErNull();
        asyncCache.fjernUregistrerteCacher();

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
        log.info("Oppdaterer cacher til avspillergruppe: {}", avspillergruppeId);
        oppdaterAlleIdenterCache(avspillergruppeId);
        oppdaterLevendeIdenterCache(avspillergruppeId);
        oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        oppdaterGifteIdenterCache(avspillergruppeId);
        return avspillergruppeId;
    }

    private List<String> oppdaterAlleIdenterCache(Long avspillergruppeId) {
        if (asyncCache.getAlleIdenterCache()== null) {
            asyncCache.setAlleIdenterCache(new HashMap<>());
        }
        return oppdaterCache(asyncCache.getAlleIdenterCache(), avspillergruppeId, tpsfFiltreringService.finnAlleIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private List<String> oppdaterLevendeIdenterCache(Long avspillergruppeId) {
        if (asyncCache.getLevendeIdenterCache() == null) {
            asyncCache.setLevendeIdenterCache(new HashMap<>());
        }
        return oppdaterCache(asyncCache.getLevendeIdenterCache(), avspillergruppeId, tpsfFiltreringService.finnLevendeIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private List<String> oppdaterDoedeOgUtvandredeIdenterCache(Long avspillergruppeId) {
        if (asyncCache.getDoedeOgUtvandredeIdenterCache() == null) {
            asyncCache.setDoedeOgUtvandredeIdenterCache(new HashMap<>());
        }
        return oppdaterCache(asyncCache.getDoedeOgUtvandredeIdenterCache(), avspillergruppeId, tpsfFiltreringService.finnDoedeOgUtvandredeIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private List<String> oppdaterGifteIdenterCache(Long avspillergruppeId) {
        if (asyncCache.getGifteIdenterCache() == null) {
            asyncCache.setGifteIdenterCache(new HashMap<>());
        }
        return oppdaterCache(asyncCache.getGifteIdenterCache(), avspillergruppeId, tpsfFiltreringService.finnGifteIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private List<String> oppdaterFoedteIdenterCache(Long avspillergruppeId) {
        if (asyncCache.getFoedteIdenterCache() == null) {
            asyncCache.setFoedteIdenterCache(new HashMap<>());
        }
        return oppdaterCache(asyncCache.getFoedteIdenterCache(), avspillergruppeId, tpsfFiltreringService.finnFoedteIdenter(avspillergruppeId)).get(avspillergruppeId);
    }

    private Map<Long, List<String>> oppdaterCache(Map<Long, List<String>> cache, Long avspillergruppeId, List<String> innhold) {
        if (cache.containsKey(avspillergruppeId)) {
            cache.get(avspillergruppeId).clear();
        }
        cache.put(avspillergruppeId, innhold);
        return cache;
    }
}
