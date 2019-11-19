package no.nav.registre.hodejegeren.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class CacheService {

    private final TpsfFiltreringService tpsfFiltreringService;

    private final AsyncCache asyncCache;

    public Set<Long> hentCachedeAvspillergruppeIder() {
        var alleIdenterCache = asyncCache.getAlleIdenterCache();
        if (alleIdenterCache == null) {
            return new HashSet<>();
        } else {
            return alleIdenterCache.keySet();
        }
    }

    public List<String> hentAlleIdenterCache(Long avspillergruppeId) {
        List<String> alleIdenter;
        var alleIdenterCache = asyncCache.getAlleIdenterCache();
        if (alleIdenterCache == null || !alleIdenterCache.containsKey(avspillergruppeId) || alleIdenterCache.get(avspillergruppeId).isEmpty()) {
            alleIdenter = asyncCache.oppdaterAlleIdenterCache(avspillergruppeId);
        } else {
            alleIdenter = alleIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterAlleIdenterCache(avspillergruppeId);
        }
        return alleIdenter;
    }

    public List<String> hentLevendeIdenterCache(Long avspillergruppeId) {
        List<String> levendeIdenter;
        var levendeIdenterCache = asyncCache.getLevendeIdenterCache();
        if (levendeIdenterCache == null || !levendeIdenterCache.containsKey(avspillergruppeId) || levendeIdenterCache.get(avspillergruppeId).isEmpty()) {
            levendeIdenter = asyncCache.oppdaterLevendeIdenterCache(avspillergruppeId);
        } else {
            levendeIdenter = levendeIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterLevendeIdenterCache(avspillergruppeId);
        }
        return levendeIdenter;
    }

    public List<String> hentDoedeOgUtvandredeIdenterCache(Long avspillergruppeId) {
        List<String> doedeOgUtvandredeIdenter;
        var doedeOgUtvandredeIdenterCache = asyncCache.getDoedeOgUtvandredeIdenterCache();
        if (doedeOgUtvandredeIdenterCache == null || !doedeOgUtvandredeIdenterCache.containsKey(avspillergruppeId) || doedeOgUtvandredeIdenterCache.get(avspillergruppeId).isEmpty()) {
            doedeOgUtvandredeIdenter = asyncCache.oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        } else {
            doedeOgUtvandredeIdenter = doedeOgUtvandredeIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
        }
        return doedeOgUtvandredeIdenter;
    }

    public List<String> hentGifteIdenterCache(Long avspillergruppeId) {
        List<String> gifteIdenter;
        var gifteIdenterCache = asyncCache.getGifteIdenterCache();
        if (gifteIdenterCache == null || !gifteIdenterCache.containsKey(avspillergruppeId) || gifteIdenterCache.get(avspillergruppeId).isEmpty()) {
            gifteIdenter = asyncCache.oppdaterGifteIdenterCache(avspillergruppeId);
        } else {
            gifteIdenter = gifteIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterGifteIdenterCache(avspillergruppeId);
        }
        return gifteIdenter;
    }

    public List<String> hentFoedteIdenterCache(Long avspillergruppeId) {
        List<String> foedteIdenter;
        var foedteIdenterCache = asyncCache.getFoedteIdenterCache();
        if (foedteIdenterCache == null || !foedteIdenterCache.containsKey(avspillergruppeId) || foedteIdenterCache.get(avspillergruppeId).isEmpty()) {
            foedteIdenter = asyncCache.oppdaterFoedteIdenterCache(avspillergruppeId);
        } else {
            foedteIdenter = foedteIdenterCache.get(avspillergruppeId);
            asyncCache.asyncOppdaterFoedteIdenterCache(avspillergruppeId);
        }
        return foedteIdenter;
    }

    public List<Long> oppdaterAlleCacher() {
        return asyncCache.oppdaterAlleCacher();
    }

    public Long oppdaterAlleCacher(Long avspillergruppeId) {
        return asyncCache.oppdaterAlleCacher(avspillergruppeId);
    }
}
