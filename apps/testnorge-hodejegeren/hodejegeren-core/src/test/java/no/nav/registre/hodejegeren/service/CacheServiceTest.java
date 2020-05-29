package no.nav.registre.hodejegeren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class CacheServiceTest {

    @Mock
    private AsyncCache asyncCache;

    @InjectMocks
    private CacheService cacheService;

    private Long avspillergruppeId = 123L;
    private String fnr1 = "01010101010";

    @Test
    public void shouldHenteAlleIdenterMedTomCache() {
        when(asyncCache.getAlleIdenterCache()).thenReturn(new HashMap<>());

        cacheService.hentAlleIdenterCache(avspillergruppeId);

        verify(asyncCache).oppdaterAlleIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteAlleIdenterMedEksisterendeCache() {
        Map<Long, List<String>> alleIdenterCache = new HashMap<>();
        alleIdenterCache.put(avspillergruppeId, Collections.singletonList(fnr1));

        when(asyncCache.getAlleIdenterCache()).thenReturn(alleIdenterCache);

        var result = cacheService.hentAlleIdenterCache(avspillergruppeId);

        assertThat(result, Matchers.containsInAnyOrder(fnr1));

        verify(asyncCache).asyncOppdaterAlleIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteLevendeIdenterMedTomCache() {
        when(asyncCache.getLevendeIdenterCache()).thenReturn(new HashMap<>());

        cacheService.hentLevendeIdenterCache(avspillergruppeId);

        verify(asyncCache).oppdaterLevendeIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteLevendeIdenterMedEksisterendeCache() {
        Map<Long, List<String>> levendeIdenterCache = new HashMap<>();
        levendeIdenterCache.put(avspillergruppeId, Collections.singletonList(fnr1));

        when(asyncCache.getLevendeIdenterCache()).thenReturn(levendeIdenterCache);

        var result = cacheService.hentLevendeIdenterCache(avspillergruppeId);

        assertThat(result, Matchers.containsInAnyOrder(fnr1));

        verify(asyncCache).asyncOppdaterLevendeIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteDoedeOgUtvandredeIdenterMedTomCache() {
        when(asyncCache.getDoedeOgUtvandredeIdenterCache()).thenReturn(new HashMap<>());

        cacheService.hentDoedeOgUtvandredeIdenterCache(avspillergruppeId);

        verify(asyncCache).oppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteDoedeOgUtvandredeIdenterMedEksisterendeCache() {
        Map<Long, List<String>> doedeOgUtvandredeIdenterCache = new HashMap<>();
        doedeOgUtvandredeIdenterCache.put(avspillergruppeId, Collections.singletonList(fnr1));

        when(asyncCache.getDoedeOgUtvandredeIdenterCache()).thenReturn(doedeOgUtvandredeIdenterCache);

        var result = cacheService.hentDoedeOgUtvandredeIdenterCache(avspillergruppeId);

        assertThat(result, Matchers.containsInAnyOrder(fnr1));

        verify(asyncCache).asyncOppdaterDoedeOgUtvandredeIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteGifteIdenterAndUpdateCache() {
        when(asyncCache.getGifteIdenterCache()).thenReturn(new HashMap<>());

        cacheService.hentGifteIdenterCache(avspillergruppeId);

        verify(asyncCache).oppdaterGifteIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteGifteIdenterMedEksisterendeCache() {
        Map<Long, List<String>> gifteIdenterCache = new HashMap<>();
        gifteIdenterCache.put(avspillergruppeId, Collections.singletonList(fnr1));

        when(asyncCache.getGifteIdenterCache()).thenReturn(gifteIdenterCache);

        var result = cacheService.hentGifteIdenterCache(avspillergruppeId);

        assertThat(result, Matchers.containsInAnyOrder(fnr1));

        verify(asyncCache).asyncOppdaterGifteIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteFoedteIdenterAndUpdateCache() {
        when(asyncCache.getFoedteIdenterCache()).thenReturn(new HashMap<>());

        cacheService.hentFoedteIdenterCache(avspillergruppeId);

        verify(asyncCache).oppdaterFoedteIdenterCache(avspillergruppeId);
    }

    @Test
    public void shouldHenteFoedteIdenterMedEksisterendeCache() {
        Map<Long, List<String>> foedteIdenterCache = new HashMap<>();
        foedteIdenterCache.put(avspillergruppeId, Collections.singletonList(fnr1));

        when(asyncCache.getFoedteIdenterCache()).thenReturn(foedteIdenterCache);

        var result = cacheService.hentFoedteIdenterCache(avspillergruppeId);

        assertThat(result, Matchers.containsInAnyOrder(fnr1));

        verify(asyncCache).asyncOppdaterFoedteIdenterCache(avspillergruppeId);
    }
}