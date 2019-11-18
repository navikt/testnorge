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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AsyncCacheTest {

    @Mock
    private TpsfFiltreringService tpsfFiltreringService;

    @InjectMocks
    private AsyncCache asyncCache;

    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private String fnr3 = "03030303030";
    private String fnr4 = "04040404040";
    private List<String> alleIdenter;
    private List<String> levendeIdenter;
    private List<String> doedeOgUtvandredeIdenter;
    private List<String> gifteIdenter;
    private List<String> foedteIdenter;
    private long avspillergruppeId = 123L;
    private List<Long> fasteAvspillergrupper;

    @Before
    public void setUp() {
        fasteAvspillergrupper = new ArrayList<>(Collections.singletonList(avspillergruppeId));

        ReflectionTestUtils.setField(asyncCache, "fasteAvspillergrupper", fasteAvspillergrupper);

        alleIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr2, fnr3, fnr4));
        levendeIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr2, fnr3));
        doedeOgUtvandredeIdenter = new ArrayList<>(Collections.singletonList(fnr4));
        gifteIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        foedteIdenter = new ArrayList<>(Arrays.asList(fnr3, fnr4));
    }

    @Test
    public void shouldOppdatereAlleCacherForFasteAvspillergrupper() {
        when(tpsfFiltreringService.finnAlleIdenter(avspillergruppeId)).thenReturn(alleIdenter);
        when(tpsfFiltreringService.finnLevendeIdenter(avspillergruppeId)).thenReturn(levendeIdenter);
        when(tpsfFiltreringService.finnDoedeOgUtvandredeIdenter(avspillergruppeId)).thenReturn(doedeOgUtvandredeIdenter);
        when(tpsfFiltreringService.finnGifteIdenter(avspillergruppeId)).thenReturn(gifteIdenter);
        when(tpsfFiltreringService.finnFoedteIdenter(avspillergruppeId)).thenReturn(foedteIdenter);

        asyncCache.oppdaterAlleCacher();

        assertThat(asyncCache.getAlleIdenterCache().get(avspillergruppeId), Matchers.containsInAnyOrder(alleIdenter.toArray()));
        assertThat(asyncCache.getLevendeIdenterCache().get(avspillergruppeId), Matchers.containsInAnyOrder(levendeIdenter.toArray()));
        assertThat(asyncCache.getDoedeOgUtvandredeIdenterCache().get(avspillergruppeId), Matchers.containsInAnyOrder(doedeOgUtvandredeIdenter.toArray()));
        assertThat(asyncCache.getGifteIdenterCache().get(avspillergruppeId), Matchers.containsInAnyOrder(gifteIdenter.toArray()));
        assertThat(asyncCache.getFoedteIdenterCache().get(avspillergruppeId), Matchers.containsInAnyOrder(foedteIdenter.toArray()));

        verify(tpsfFiltreringService).finnAlleIdenter(avspillergruppeId);
        verify(tpsfFiltreringService).finnLevendeIdenter(avspillergruppeId);
        verify(tpsfFiltreringService).finnDoedeOgUtvandredeIdenter(avspillergruppeId);
        verify(tpsfFiltreringService).finnGifteIdenter(avspillergruppeId);
        verify(tpsfFiltreringService).finnFoedteIdenter(avspillergruppeId);
    }
}