package no.nav.registre.hodejegeren.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Setter
@RequiredArgsConstructor
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

    private void fjernCache(Map<Long, List<String>> cache) {
        for (Long avspillergruppeId : cache.keySet()) {
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
