package no.nav.registre.hodejegeren.service;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static no.nav.registre.hodejegeren.service.HodejegerService.TRANSAKSJONSTYPE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EksisterendeIdenterServiceTest {

    private final String miljoe = "t1";
    private Map<String, String> status;

    @Mock
    private Random rand;

    @Mock
    private TpsStatusQuoService tpsStatusQuoService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Before
    public void init() throws IOException {

        Set<String> levendeIdenter = new LinkedHashSet<>();
        levendeIdenter.add("20044249945");
        levendeIdenter.add("20044249946");
        levendeIdenter.add("20044249947");
        levendeIdenter.add("20044249948");

        Set<String> doedeIdenter = new LinkedHashSet<>();
        doedeIdenter.add("20044249948");

        //Liste med identer og tom liste med identer for å teste å hente voksne identer
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(1L, Arrays.asList(
                FOEDSELSMELDING.getAarsakskode(),
                INNVANDRING.getAarsakskode(),
                FOEDSELSNUMMERKORREKSJON.getAarsakskode(),
                TILDELING_DNUMMER.getAarsakskode()), TRANSAKSJONSTYPE)).thenReturn(levendeIdenter);
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(3L, Arrays.asList(
                FOEDSELSMELDING.getAarsakskode(),
                INNVANDRING.getAarsakskode(),
                FOEDSELSNUMMERKORREKSJON.getAarsakskode(),
                TILDELING_DNUMMER.getAarsakskode()), TRANSAKSJONSTYPE)).thenReturn(levendeIdenter);
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(3L, Arrays.asList(
                Endringskoder.DOEDSMELDING.getAarsakskode(),
                Endringskoder.UTVANDRING.getAarsakskode()), TRANSAKSJONSTYPE)).thenReturn(doedeIdenter);

        status = new HashMap<>();
        status.put("datoDo", "");

        when(tpsStatusQuoService.hentStatusQuo("FS03-FDNUMMER-PERSDATA-O", Arrays.asList("datoDo", "statsborger"), miljoe, "20044249945")).thenReturn(status);
        when(tpsStatusQuoService.hentStatusQuo("FS03-FDNUMMER-PERSDATA-O", Arrays.asList("datoDo", "statsborger"), miljoe, "20044249946")).thenReturn(status);
        when(tpsStatusQuoService.hentStatusQuo("FS03-FDNUMMER-PERSDATA-O", Arrays.asList("datoDo", "statsborger"), miljoe, "20044249947")).thenReturn(status);
        when(tpsStatusQuoService.hentStatusQuo("FS03-FDNUMMER-PERSDATA-O", Arrays.asList("datoDo", "statsborger"), miljoe, "20044249948")).thenReturn(status);
    }

    @Test
    public void hentVokseneIdenterIGruppeTest() throws IOException {
        for (int i = 0; i < 3; i++) {
            List<String> identer = eksisterendeIdenterService.hentVokseneIdenterIGruppe(1L, miljoe, i);
            assertEquals(i, identer.size());
        }
    }

    @Test
    public void hentVoksneIdenterIGruppeIngenIdenterTest() {
        List<String> identer = eksisterendeIdenterService.hentVokseneIdenterIGruppe(2L, miljoe, 2);
        assertEquals(0, identer.size());
    }

    @Test
    public void hentVoksneIdenterIGruppeForMangeAaHenteTest() {
        List<String> identer = eksisterendeIdenterService.hentVokseneIdenterIGruppe(1L, miljoe, 6);
        assertEquals(4, identer.size());
    }

    @Test
    public void hentVoksneIdenterIGruppeEnDoedITPS() throws IOException {

        Map<String, String> statusDoed = new HashMap<>();
        statusDoed.put("datoDo", "12312");
        when(tpsStatusQuoService.hentStatusQuo("FS03-FDNUMMER-PERSDATA-O", Arrays.asList("datoDo", "statsborger"), miljoe, "20044249948")).thenReturn(statusDoed);
        List<String> identer = eksisterendeIdenterService.hentVokseneIdenterIGruppe(1L, miljoe, 10);
        assertEquals(3, identer.size());

    }

    @Test
    public void finnLevendeIdenterTest() {
        List<String> levende = eksisterendeIdenterService.finnLevendeIdenter(3L);
        assertEquals(3, levende.size());
        assertThat(levende, containsInAnyOrder(
                "20044249945",
                "20044249946",
                "20044249947"
        ));
    }

    @Test
    public void finnDoedeOgUtvandredeIdenterTest() {
        List<String> doede = eksisterendeIdenterService.finnDoedeOgUtvandredeIdenter(3L);
        assertEquals(1, doede.size());
    }

    @Test
    public void finnGifteIdenterTest() {

    }

}
