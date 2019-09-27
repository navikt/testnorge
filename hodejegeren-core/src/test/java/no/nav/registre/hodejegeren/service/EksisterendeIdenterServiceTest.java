package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.provider.rs.HodejegerenController.MAX_ALDER;
import static no.nav.registre.hodejegeren.provider.rs.HodejegerenController.MIN_ALDER;
import static no.nav.registre.hodejegeren.service.EksisterendeIdenterService.TRANSAKSJONSTYPE;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET_BESKRIVELSE;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.provider.rs.responses.NavEnhetResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.persondata.PersondataResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.relasjon.RelasjonsResponse;

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

        Set<String> foedteIdenter = new LinkedHashSet<>();
        foedteIdenter.add("20044251231");

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
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(3L, Arrays.asList(
                FOEDSELSMELDING.getAarsakskode()), TRANSAKSJONSTYPE)).thenReturn(foedteIdenter);

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
     * Henter identer, men en ident er ikke synkronisert med tps og har mottatt en dødsmelding
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
     * Scenario:
     * Finn fødte identer
     */
    @Test
    public void finnFoedteIdenterTest() {
        List<String> foedte = eksisterendeIdenterService.finnFoedteIdenter(3L);
        assertEquals(1, foedte.size());
        assertThat(foedte, containsInAnyOrder(
                "20044251231"
        ));
    }

    /**
     * Gitt en liste med identer, skal systemet finne tilhørende nav-kontor og returnere et map med fnr-navKontor
     */
    @Test
    public void hentFnrMedNavKontor() throws IOException {
        Map<String, String> status = new HashMap<>();
        status.put(NAV_ENHET, "123");
        status.put(NAV_ENHET_BESKRIVELSE, "test");
        when(tpsStatusQuoService.hentStatusQuo(eq("FS03-FDNUMMER-KERNINFO-O"), eq(Arrays.asList(NAV_ENHET, NAV_ENHET_BESKRIVELSE)), eq(miljoe), any())).thenReturn(status);

        List<String> levendeIdenter = new ArrayList<>();
        levendeIdenter.add("20044249945");
        levendeIdenter.add("20044249946");

        List<NavEnhetResponse> navEnhetResponse = eksisterendeIdenterService.hentFnrMedNavKontor(miljoe, levendeIdenter);

        assertThat(navEnhetResponse.get(0).getIdent(), equalTo(levendeIdenter.get(0)));
        assertThat(navEnhetResponse.get(0).getNavEnhet(), equalTo("123"));
        assertThat(navEnhetResponse.get(0).getNavEnhetBeskrivelse(), equalTo("test"));

        assertThat(navEnhetResponse.get(1).getIdent(), equalTo(levendeIdenter.get(1)));
        assertThat(navEnhetResponse.get(1).getNavEnhet(), equalTo("123"));
        assertThat(navEnhetResponse.get(1).getNavEnhetBeskrivelse(), equalTo("test"));
    }

    /**
     * Gitt et antall identer med tilhørende miljø i TPS, returner en liste med objekter med fnr og tilhørende nav-enhet
     */
    @Test
    public void hentGittAntallIdenterMedStatusQuoTest() throws IOException {
        URL jsonContent = Resources.getResource("FS03-FDNUMMER-KERNINFO-O.json");
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);
        String fnr1 = "23048801390";
        Set<String> identSet = new LinkedHashSet<>();
        identSet.add(fnr1);

        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(anyLong(), anyList(), anyString())).thenReturn(identSet);
        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                anyLong(),
                eq(Arrays.asList(
                        Endringskoder.DOEDSMELDING.getAarsakskode(),
                        Endringskoder.UTVANDRING.getAarsakskode())),
                anyString()))
                .thenReturn(Collections.emptySet());
        when(tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, fnr1)).thenReturn(jsonNode);

        Map<String, JsonNode> fnrMedStatusQuo = eksisterendeIdenterService.hentGittAntallIdenterMedStatusQuo(1L, miljoe, identSet.size(), MIN_ALDER, MAX_ALDER);

        verify(tpsfConsumer, times(2)).getIdenterFiltrertPaaAarsakskode(anyLong(), anyList(), anyString());
        verify(tpsStatusQuoService).getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, fnr1);

        assertThat(fnrMedStatusQuo.get(fnr1), equalTo(jsonNode));
    }

    @Test
    public void shouldHenteAdresserPaaIdent() throws IOException {
        URL jsonContent = Resources.getResource("FS03-FDNUMMER-KERNINFO-O.json");
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);
        String fnr1 = "23048801390";
        List<String> identer = new ArrayList<>(Collections.singleton(fnr1));

        when(tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, identer.get(0))).thenReturn(jsonNode);

        Map<String, JsonNode> adressePaaIdenter = eksisterendeIdenterService.hentAdressePaaIdenter(miljoe, identer);

        assertThat(adressePaaIdenter.get("23048801390").findValue("boAdresse1").asText(), equalTo("WALLDORFERSTRABE 1289"));
    }

    @Test
    public void shouldHentePersondataPaaIdent() throws IOException {
        String fnr = "12101816735";
        String miljoe = "t1";

        JsonNode jsonNode = new ObjectMapper().readTree(Resources.getResource("persondata/persondata.json"));

        when(tpsStatusQuoService.getInfoOnRoutineName(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonNode);
        PersondataResponse response = eksisterendeIdenterService.hentPersondata(fnr, miljoe);

        assertThat(response.getFnr(), equalTo(fnr));
        assertThat(response.getFornavn(), equalTo("USTABIL"));
        assertThat(response.getEtternavn(), equalTo("PARASOLL"));
        assertThat(response.getStatsborger(), equalTo("NORGE"));
    }

    @Test
    public void shouldHandleEmptyRelasjon() throws IOException {
        String fnr = "12090080405";
        String miljoe = "t1";

        JsonNode jsonNode = new ObjectMapper().readTree(Resources.getResource("relasjoner/tom_relasjon.json"));

        when(tpsStatusQuoService.getInfoOnRoutineName(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonNode);
        RelasjonsResponse response = eksisterendeIdenterService.hentRelasjoner(fnr, miljoe);

        assertThat(response.getFnr(), equalTo(fnr));
        assertThat(response.getRelasjoner().size(), is(0));
    }

    @Test
    public void shouldHandleSingleRelasjon() throws IOException {
        String fnr = "12090080405";
        String miljoe = "t1";

        JsonNode jsonNode = new ObjectMapper().readTree(Resources.getResource("relasjoner/relasjon.json"));

        when(tpsStatusQuoService.getInfoOnRoutineName(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonNode);
        RelasjonsResponse response = eksisterendeIdenterService.hentRelasjoner(fnr, miljoe);

        assertThat(response.getFnr(), equalTo(fnr));
        assertThat(response.getRelasjoner().size(), is(1));
        assertThat(response.getRelasjoner().get(0).getFnrRelasjon(), equalTo("12021790069"));
    }

    @Test
    public void shouldHandleMultipleRelasjoner() throws IOException {
        String fnr = "12090080405";
        String miljoe = "t1";

        JsonNode jsonNode = new ObjectMapper().readTree(Resources.getResource("relasjoner/relasjoner.json"));

        when(tpsStatusQuoService.getInfoOnRoutineName(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonNode);
        RelasjonsResponse response = eksisterendeIdenterService.hentRelasjoner(fnr, miljoe);

        assertThat(response.getFnr(), equalTo(fnr));
        assertThat(response.getRelasjoner().size(), is(2));
        assertThat(response.getRelasjoner().get(0).getFnrRelasjon(), equalTo("12021790069"));
        assertThat(response.getRelasjoner().get(1).getFnrRelasjon(), equalTo("17120080318"));
    }

    @Test
    public void shouldHenteIdenterNotInTps() throws IOException {
        Long avspillergruppeId = 123L;
        String fnr1 = "20092943861";
        String fnr2 = "12345678910";
        List<String> identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        JsonNode jsonNode = new ObjectMapper().readTree(Resources.getResource("tpsStatus/tps_status.json"));

        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(eq(avspillergruppeId), anyList(), anyString())).thenReturn(new HashSet<>(identer));
        when(tpsfConsumer.hentTpsStatusPaaIdenter(eq("A0"), eq(miljoe), anyList())).thenReturn(jsonNode);

        List<String> identerIkkeITps = eksisterendeIdenterService.hentIdenterSomIkkeErITps(avspillergruppeId, miljoe);

        assertThat(identerIkkeITps, contains(fnr2));
        assertThat(identerIkkeITps, not(contains(fnr1)));
    }

    @Test
    public void shouldHenteIdenterSomKolliderer() throws IOException {
        Long avspillergruppeId = 123L;
        String fnr1 = "20092943861";
        String fnr2 = "12345678910";
        List<String> identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        JsonNode jsonNode = new ObjectMapper().readTree(Resources.getResource("tpsStatus/tps_kollisjon.json"));

        when(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(eq(avspillergruppeId), anyList(), anyString())).thenReturn(new HashSet<>(identer));
        when(tpsfConsumer.hentTpsStatusPaaIdenter(eq("A2"), eq("q2"), anyList())).thenReturn(jsonNode);

        List<String> identerSomKolliderer = eksisterendeIdenterService.hentIdenterSomKolliderer(avspillergruppeId);

        assertThat(identerSomKolliderer, contains(fnr1));
        assertThat(identerSomKolliderer, not(contains(fnr2)));
    }
}
