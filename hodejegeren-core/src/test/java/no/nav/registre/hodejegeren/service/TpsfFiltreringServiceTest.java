package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EksisterendeIdenterService.TRANSAKSJONSTYPE;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
import java.util.List;
import java.util.Set;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class TpsfFiltreringServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private TpsfFiltreringService tpsfFiltreringService;

    private Long avspillergruppeId = 1L;

    @Before
    public void setUp() {
        Set<String> levendeIdenter = new LinkedHashSet<>();
        levendeIdenter.add("20044249945");
        levendeIdenter.add("20044249946");
        levendeIdenter.add("20044249947");
        levendeIdenter.add("20044249948");

        Set<String> doedeIdenter = new LinkedHashSet<>();
        doedeIdenter.add("20044249948");

        Set<String> gifteIdenter = new LinkedHashSet<>();
        gifteIdenter.add("20041751231");

        Set<String> foedteIdenter = new LinkedHashSet<>();
        foedteIdenter.add("20041751231");

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
        assertEquals(4, alle.size());
        assertThat(alle, containsInAnyOrder(
                "20044249945",
                "20044249946",
                "20044249947",
                "20044249948"
        ));
    }

    @Test
    public void finnLevendeIdenterTest() {
        var levende = tpsfFiltreringService.finnLevendeIdenter(avspillergruppeId);
        assertEquals(3, levende.size());
        assertThat(levende, containsInAnyOrder(
                "20044249945",
                "20044249946",
                "20044249947"
        ));
    }

    @Test
    public void finnDoedeOgUtvandredeIdenterTest() {
        var doede = tpsfFiltreringService.finnDoedeOgUtvandredeIdenter(avspillergruppeId);
        assertEquals(1, doede.size());
        assertThat(doede, containsInAnyOrder(
                "20044249948"
        ));
    }

    @Test
    public void finnGifteIdenterTest() {
        var gifte = tpsfFiltreringService.finnGifteIdenter(avspillergruppeId);
        assertEquals(1, gifte.size());
        assertThat(gifte, containsInAnyOrder(
                "20041751231"
        ));
    }

    @Test
    public void finnFoedteIdenterTest() {
        var foedte = tpsfFiltreringService.finnFoedteIdenter(avspillergruppeId);
        assertEquals(1, foedte.size());
        assertThat(foedte, containsInAnyOrder(
                "20041751231"
        ));
    }
}