package no.nav.registre.orkestratoren.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class SyntDataInfoServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private SyntDataInfoService syntDataInfoService;

    /**
     * Testscenario: Metoden henter liste med innvandrede og fødte personers FNR
     * fra Tpsf via TpsfConsumer, og deretter henter liste med FNR den utvandrede og døde personer.
     * Responsen er resultatet av at sistnevnte liste trekkes fra førstnevnte.
     */
    @Test
    public void opprettListenLevendeNordmenn() {
        long gruppeId = 123L;

        List<String> opprettedeFnr = Arrays.asList("11111111111", "22222222222", "33333333333", "44444444444");
        List<String> doedeOgUtvandredeFnr = Arrays.asList("33333333333", "44444444444");
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(gruppeId, Arrays.asList("01", "02", "39"), "1"))
                .thenReturn(new LinkedHashSet(opprettedeFnr));
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(gruppeId, Arrays.asList("43", "32"), "1"))
                .thenReturn(new LinkedHashSet(doedeOgUtvandredeFnr));

        List<String> levendeNordmenn = syntDataInfoService.opprettListenLevendeNordmenn(gruppeId);

        assertEquals(2, levendeNordmenn.size());
        assertEquals(Arrays.asList("11111111111", "22222222222"), levendeNordmenn);
    }
}