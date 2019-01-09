package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static no.nav.registre.hodejegeren.service.HodejegerService.TRANSAKSJONSTYPE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class EksisterendeIdenterServiceTest {

    private static final String ROUTINE_PERSDATA = "FS03-FDNUMMER-PERSDATA-O";

    private final String miljoe = "t1";

    private final List<String> statusFelter = Arrays.asList("datoDo", "statsborger");

    @Mock
    private Random rand;

    @Mock
    private TpsStatusQuoService tpsStatusQuoService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Before
    public void setUp() throws IOException {

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

        Map<String, String> status = new HashMap<>();
        status.put("datoDo", "");

        when(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, statusFelter, miljoe, "20044249945")).thenReturn(status);
        when(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, statusFelter, miljoe, "20044249946")).thenReturn(status);
        when(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, statusFelter, miljoe, "20044249947")).thenReturn(status);
        when(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, statusFelter, miljoe, "20044249948")).thenReturn(status);
    }

    /**
     * Scenario:
     * Henter et varierende antall FNR 0-2
     */
    @Test
    public void hentVokseneIdenterIGruppeTest() {
        for (int i = 0; i < 3; i++) {
            List<String> identer = eksisterendeIdenterService.hentMyndigeIdenterIAvspillerGruppe(1L, miljoe, i);
            assertEquals(i, identer.size());
        }
    }

    /**
     * Scenario:
     * Henter identer fra en tom gruppe
     */
    @Test
    public void hentVoksneIdenterIGruppeIngenIdenterTest() {
        List<String> identer = eksisterendeIdenterService.hentMyndigeIdenterIAvspillerGruppe(2L, miljoe, 2);
        assertTrue(identer.isEmpty());
    }

    /**
     * Scenario:
     * Henter for mange identer i forhold til hvor mange som eksisterer i avspillergruppa
     */
    @Test
    public void hentVoksneIdenterIGruppeForMangeAaHenteTest() {
        List<String> identer = eksisterendeIdenterService.hentMyndigeIdenterIAvspillerGruppe(1L, miljoe, 6);
        assertEquals(4, identer.size());
        assertThat(identer, containsInAnyOrder(
                "20044249945",
                "20044249946",
                "20044249947",
                "20044249948"
        ));
    }

    /**
     * Scenario:
     * Henter identer, men en ident er ikke synkronisert med tps og har motatt en dødsmelding
     *
     * @throws IOException
     */
    @Test
    public void hentVoksneIdenterIGruppeEnDoedITPS() throws IOException {

        Map<String, String> statusDoed = new HashMap<>();
        statusDoed.put("datoDo", "12312");
        when(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, statusFelter, miljoe, "20044249948")).thenReturn(statusDoed);
        List<String> identer = eksisterendeIdenterService.hentMyndigeIdenterIAvspillerGruppe(1L, miljoe, 10);
        assertEquals(3, identer.size());
        assertThat(identer, containsInAnyOrder(
                "20044249945",
                "20044249946",
                "20044249947"
        ));

    }

    /**
     * Scenario:
     * Finn levende identer
     */
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

    /**
     * Scenario:
     * Finn døde og utvandrede identer
     */
    @Test
    public void finnDoedeOgUtvandredeIdenterTest() {
        List<String> doede = eksisterendeIdenterService.finnDoedeOgUtvandredeIdenter(3L);
        assertEquals(1, doede.size());
        assertThat(doede, containsInAnyOrder(
                "20044249948"
        ));
    }
}
