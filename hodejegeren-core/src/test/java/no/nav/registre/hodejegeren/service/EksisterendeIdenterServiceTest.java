package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EksisterendeIdenterService.TRANSAKSJONSTYPE;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    private static final String ROUTINE_KERNINFO = "FS03-FDNUMMER-KERNINFO-O";
    private static final String AKSJONSKODE = "B0";
    private static final int MINIMUM_ALDER = 18;

    private final String miljoe = "t1";

    private final List<String> statusFelter = Arrays.asList(DATO_DO, STATSBORGER);

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
        status.put(DATO_DO, "");

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
    public void hentMyndigeIdenterIGruppeTest() {
        for (int i = 0; i < 3; i++) {
            List<String> identer = eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(1L, miljoe, i, MINIMUM_ALDER);
            assertEquals(i, identer.size());
        }
    }

    /**
     * Scenario:
     * Henter identer fra en tom gruppe
     */
    @Test
    public void hentMyndigeIdenterIGruppeIngenIdenterTest() {
        List<String> identer = eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(2L, miljoe, 2, MINIMUM_ALDER);
        assertTrue(identer.isEmpty());
    }

    /**
     * Scenario:
     * Henter for mange identer i forhold til hvor mange som eksisterer i avspillergruppa
     */
    @Test
    public void hentMyndigeIdenterIGruppeForMangeAaHenteTest() {
        List<String> identer = eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(1L, miljoe, 6, MINIMUM_ALDER);
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
    public void hentMyndigeIdenterIGruppeEnDoedITPS() throws IOException {

        Map<String, String> statusDoed = new HashMap<>();
        statusDoed.put(DATO_DO, "12312");
        when(tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, statusFelter, miljoe, "20044249948")).thenReturn(statusDoed);
        List<String> identer = eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(1L, miljoe, 10, MINIMUM_ALDER);
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

    /**
     * Gitt en liste med identer, skal systemet finne tilhørende nav-kontor og returnere et map med fnr-navKontor
     */
    @Test
    public void hentFnrMedNavKontor() throws IOException {
        Map<String, String> status = new HashMap<>();
        status.put(NAV_ENHET, "123");
        when(tpsStatusQuoService.hentStatusQuo(eq("FS03-FDNUMMER-KERNINFO-O"), eq(Arrays.asList(NAV_ENHET)), eq(miljoe), any())).thenReturn(status);

        List<String> levendeIdenter = new ArrayList<>();
        levendeIdenter.add("20044249945");
        levendeIdenter.add("20044249946");

        Map<String, String> fnrMedNavKontor = eksisterendeIdenterService.hentFnrMedNavKontor(miljoe, levendeIdenter);

        assertThat(fnrMedNavKontor.get(levendeIdenter.get(0)), equalTo("123"));
        assertThat(fnrMedNavKontor.get(levendeIdenter.get(1)), equalTo("123"));
    }

    /**
     * Gitt et antall identer med tilhørende miljø i TPS, returner et map med ident->status-quo-kobling
     */
    @Test
    public void hentGittAntallIdenterMedStatusQuoTest() throws IOException {
        URL jsonContent = Resources.getResource("FS03-FDNUMMER-KERNINFO-O.json");
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);
        String fnr1 = "23048801390";
        Set<String> identSet = new LinkedHashSet<>();
        identSet.add(fnr1);

        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(anyLong(), anyList(), anyString())).thenReturn(identSet);
        when(tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, fnr1)).thenReturn(jsonNode);

        Map<String, JsonNode> fnrMedStatusQuo = eksisterendeIdenterService.hentGittAntallIdenterMedStatusQuo(1L, miljoe, identSet.size());

        verify(tpsfConsumer).getIdenterFiltrertPaaAarsakskode(anyLong(), anyList(), anyString());
        verify(tpsStatusQuoService).getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, fnr1);

        assertThat(fnrMedStatusQuo.get(fnr1), equalTo(jsonNode));
    }
}
