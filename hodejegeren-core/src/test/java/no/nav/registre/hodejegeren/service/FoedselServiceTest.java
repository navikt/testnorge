package no.nav.registre.hodejegeren.service;

import no.nav.registre.hodejegeren.consumer.IdentPoolConsumer;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FoedselServiceTest {

    @Mock
    private IdentPoolConsumer identPoolConsumer;

    @Mock
    private Random rand;

    @InjectMocks
    private FoedselService foedselService;

    /**
     * Testscenario: HVIS det skal opprettes fødselsmelding, skal systemet i metoden {@link FoedselService#findMoedre},
     * hente en eksisterende ident som kan være mor til barnet.
     */
    @Test
    public void shouldFindEksisterendeIdent() {
        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.add("01010101010");
        levendeIdenterINorge.add("02020202020");
        levendeIdenterINorge.add("03030303030");
        int firstIdentIndexInList = 0;

        when(rand.nextInt(anyInt())).thenReturn(0);

        List<String> potensielleMoedre = foedselService.findMoedre(1, levendeIdenterINorge);

        assertEquals(levendeIdenterINorge.get(firstIdentIndexInList), potensielleMoedre.get(firstIdentIndexInList));
    }

    /**
     * Testscenario: Systemet skal opprette relasjonen fra barn til mor i metoden {@link FoedselService#behandleFoedselsmeldinger},
     * påse at mor er eldre enn barn, og opprette barn til mor-relasjonen.
     */
    @Test
    public void shouldFindChildForMother() {
        List<RsMeldingstype> meldinger;
        meldinger = new ArrayList<>();
        meldinger.add(new RsMeldingstype1Felter());

        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.add("01010101010");

        String barnFnr = "10101054321";

        when(identPoolConsumer.hentNyeIdenter(any())).thenReturn(Arrays.asList(barnFnr));

        List<String> opprettedeBarn = foedselService.behandleFoedselsmeldinger(FNR, meldinger, levendeIdenterINorge);

        assertTrue(opprettedeBarn.contains(barnFnr));
    }
}
