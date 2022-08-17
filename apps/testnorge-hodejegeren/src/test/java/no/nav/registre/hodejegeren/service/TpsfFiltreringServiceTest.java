package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EksisterendeIdenterService.TRANSAKSJONSTYPE;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class TpsfFiltreringServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private TpsfFiltreringService tpsfFiltreringService;

    private Long avspillergruppeId = 1L;
    private static final String FNR1 = "20044249945";
    private static final String FNR2 = "20044249946";
    private static final String FNR3 = "20044249947";
    private static final String FNR4 = "20044249948";
    private static final String FNR5 = "20041751231";

    @Before
    public void setUp() {
        Set<String> levendeIdenter = new LinkedHashSet<>();
        levendeIdenter.add(FNR1);
        levendeIdenter.add(FNR2);
        levendeIdenter.add(FNR3);
        levendeIdenter.add(FNR4);

        Set<String> doedeIdenter = new LinkedHashSet<>();
        doedeIdenter.add(FNR4);

        Set<String> gifteIdenter = new LinkedHashSet<>();
        gifteIdenter.add(FNR5);

        Set<String> foedteIdenter = new LinkedHashSet<>();
        foedteIdenter.add(FNR5);

        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(avspillergruppeId, Arrays.asList(
                FOEDSELSMELDING.getAarsakskode(),
                INNVANDRING.getAarsakskode(),
                FOEDSELSNUMMERKORREKSJON.getAarsakskode(),
                TILDELING_DNUMMER.getAarsakskode()), TRANSAKSJONSTYPE)).thenReturn(levendeIdenter);
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(avspillergruppeId, Arrays.asList(
                Endringskoder.DOEDSMELDING.getAarsakskode(),
                Endringskoder.UTVANDRING.getAarsakskode()), TRANSAKSJONSTYPE)).thenReturn(doedeIdenter);
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(avspillergruppeId, Collections.singletonList(
                Endringskoder.VIGSEL.getAarsakskode()), TRANSAKSJONSTYPE)).thenReturn(gifteIdenter);
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(avspillergruppeId, Collections.singletonList(
                FOEDSELSMELDING.getAarsakskode()), TRANSAKSJONSTYPE)).thenReturn(foedteIdenter);
    }

    @Test
    public void finnAlleIdenterTest() {
        var alle = tpsfFiltreringService.finnAlleIdenter(avspillergruppeId);
        
        assertThat(alle).hasSize(4).contains(FNR1, FNR2, FNR3, FNR4);
    }

    @Test
    public void finnLevendeIdenterTest() {
        var levende = tpsfFiltreringService.finnLevendeIdenter(avspillergruppeId);

        assertThat(levende).hasSize(3).contains(FNR1, FNR2, FNR3);
    }

    @Test
    public void finnDoedeOgUtvandredeIdenterTest() {
        var doede = tpsfFiltreringService.finnDoedeOgUtvandredeIdenter(avspillergruppeId);

        assertThat(doede).hasSize(1).contains(FNR4);
    }

    @Test
    public void finnGifteIdenterTest() {
        var gifte = tpsfFiltreringService.finnGifteIdenter(avspillergruppeId);

        assertThat(gifte).hasSize(1).contains(FNR5);
    }

    @Test
    public void finnFoedteIdenterTest() {
        var foedte = tpsfFiltreringService.finnFoedteIdenter(avspillergruppeId);

        assertThat(foedte).hasSize(1).contains(FNR5);
    }
}