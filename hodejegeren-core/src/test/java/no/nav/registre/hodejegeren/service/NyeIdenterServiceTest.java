package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.hodejegeren.consumer.IdentPoolConsumer;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

@RunWith(MockitoJUnitRunner.class)
public class NyeIdenterServiceTest {

    @Mock
    private IdentPoolConsumer identPoolConsumer;

    @InjectMocks
    private NyeIdenterService service;

    @Test
    public void shouldInsertNewIdentsIntoSkdInnvandringAndFoedselsmelding() {
        RsMeldingstype1Felter foedselsmelding = new RsMeldingstype1Felter();
        foedselsmelding.setAarsakskode("01");
        RsMeldingstype1Felter innvandringsmelding = new RsMeldingstype1Felter();
        innvandringsmelding.setAarsakskode("02");
        List<RsMeldingstype> listOfEndringsmeldinger = new ArrayList<>(Arrays.asList(foedselsmelding, innvandringsmelding));

        final String expectedFNR1 = "11111111111";
        final String expectedFNR2 = "22222222222";

        when(identPoolConsumer.hentNyeIdenter(any())).thenReturn(Arrays.asList(expectedFNR1, expectedFNR2));

        final List<String> nyeIdenter = service.settInnNyeIdenterITrans1Meldinger(FNR, listOfEndringsmeldinger);

        assertEquals(2, nyeIdenter.size());
        assertEquals(expectedFNR1, foedselsmelding.getFodselsdato() + foedselsmelding.getPersonnummer());
        assertEquals(expectedFNR2, innvandringsmelding.getFodselsdato() + innvandringsmelding.getPersonnummer());
    }

    @Test
    public void shouldProduceStatsborgerendringsmeldingAlongWithInnvandringsmelding() {
        RsMeldingstype1Felter innvandringsmelding = new RsMeldingstype1Felter();
        innvandringsmelding.setAarsakskode("02");

        final String expectedFNR1 = "11111111111";
        List<String> expectedIdenter = new ArrayList<>(Arrays.asList(expectedFNR1));

        when(identPoolConsumer.hentNyeIdenter(any())).thenReturn(expectedIdenter);

        List<RsMeldingstype> meldinger = new ArrayList<>(Arrays.asList(innvandringsmelding));

        service.settInnNyeIdenterITrans1Meldinger(FNR, meldinger);

        assertEquals(2, meldinger.size());
        assertEquals("02", meldinger.get(0).getAarsakskode());
        assertEquals("35", meldinger.get(1).getAarsakskode());
    }
}