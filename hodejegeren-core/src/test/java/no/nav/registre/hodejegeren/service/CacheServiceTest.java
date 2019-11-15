package no.nav.registre.hodejegeren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CacheServiceTest {

    @Mock
    private EksisterendeIdenterService eksisterendeIdenterService;

    @InjectMocks
    private CacheService cacheService;

    private Long avspillergruppeId = 123L;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> identer;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
    }

    @Test
    public void shouldHenteAlleIdenterAndUpdateCache() {
        when(eksisterendeIdenterService.finnAlleIdenter(avspillergruppeId)).thenReturn(identer);

        cacheService.hentAlleIdenterCache(avspillergruppeId);

        verify(eksisterendeIdenterService).finnAlleIdenter(avspillergruppeId);

        assertThat(cacheService.getAlleIdenterCache().get(avspillergruppeId).get(0), Matchers.equalTo(fnr1));
        assertThat(cacheService.getAlleIdenterCache().get(avspillergruppeId).get(1), Matchers.equalTo(fnr2));
    }

    @Test
    public void shouldHenteLevendeIdenterAndUpdateCache() {
        when(eksisterendeIdenterService.finnLevendeIdenter(avspillergruppeId)).thenReturn(identer);

        cacheService.hentLevendeIdenterCache(avspillergruppeId);

        verify(eksisterendeIdenterService).finnLevendeIdenter(avspillergruppeId);

        assertThat(cacheService.getLevendeIdenterCache().get(avspillergruppeId).get(0), Matchers.equalTo(fnr1));
        assertThat(cacheService.getLevendeIdenterCache().get(avspillergruppeId).get(1), Matchers.equalTo(fnr2));
    }

    @Test
    public void shouldHenteDoedeOgUtvandredeIdenterAndUpdateCache() {
        when(eksisterendeIdenterService.finnDoedeOgUtvandredeIdenter(avspillergruppeId)).thenReturn(identer);

        cacheService.hentDoedeOgUtvandredeIdenterCache(avspillergruppeId);

        verify(eksisterendeIdenterService).finnDoedeOgUtvandredeIdenter(avspillergruppeId);

        assertThat(cacheService.getDoedeOgUtvandredeIdenterCache().get(avspillergruppeId).get(0), Matchers.equalTo(fnr1));
        assertThat(cacheService.getDoedeOgUtvandredeIdenterCache().get(avspillergruppeId).get(1), Matchers.equalTo(fnr2));
    }

    @Test
    public void shouldHenteGifteIdenterAndUpdateCache() {
        when(eksisterendeIdenterService.finnGifteIdenter(avspillergruppeId)).thenReturn(identer);

        cacheService.hentGifteIdenterCache(avspillergruppeId);

        verify(eksisterendeIdenterService).finnGifteIdenter(avspillergruppeId);

        assertThat(cacheService.getGifteIdenterCache().get(avspillergruppeId).get(0), Matchers.equalTo(fnr1));
        assertThat(cacheService.getGifteIdenterCache().get(avspillergruppeId).get(1), Matchers.equalTo(fnr2));
    }
}