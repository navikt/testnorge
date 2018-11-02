package no.nav.registre.hodejegeren.service;

import no.nav.registre.hodejegeren.consumer.IdentPoolConsumer;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FoedselServiceTest {

    @Mock
    private IdentPoolConsumer identPoolConsumer;

    @Mock
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Mock
    private Random rand;

    @InjectMocks
    private FoedselService foedselService;

    @Test
    public void shouldFindEksisterendeIdent() {
        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.add("01010101010");
        levendeIdenterINorge.add("02020202020");
        levendeIdenterINorge.add("03030303030");

        when(rand.nextInt(anyInt())).thenReturn(0);

        assertTrue(foedselService.findMoedre(1, levendeIdenterINorge).contains(levendeIdenterINorge.get(0)));
    }

    @Test
    public void shouldFindChildForMother() {
        List<RsMeldingstype> meldinger;
        meldinger = new ArrayList<>();
        meldinger.add(new RsMeldingstype1Felter());

        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.add("01010101010");
        LocalDate morsFoedselsdato = LocalDate.of(1901, 01, 01);

        String barnFnr = "10101054321";

        when(eksisterendeIdenterService.getFoedselsdatoFraFnr(any())).thenReturn(morsFoedselsdato);

        when(identPoolConsumer.hentNyeIdenter(any())).thenReturn(Arrays.asList(barnFnr));

        assertTrue(foedselService.behandleFoedselsmeldinger(FNR, meldinger, levendeIdenterINorge).contains(barnFnr));

        verify(eksisterendeIdenterService, times(meldinger.size())).getFoedselsdatoFraFnr(any());
    }
}
