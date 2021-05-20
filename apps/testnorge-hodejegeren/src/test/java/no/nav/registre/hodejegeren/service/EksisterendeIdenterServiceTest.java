package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.provider.rs.HodejegerenController.MAX_ALDER;
import static no.nav.registre.hodejegeren.provider.rs.HodejegerenController.MIN_ALDER;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET_BESKRIVELSE;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.provider.rs.responses.kontoinfo.KontoinfoResponse;

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

    @Mock
    private TpsfFiltreringService tpsfFiltreringService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private EksisterendeIdenterService eksisterendeIdenterService;

    private Long avspillergruppeId1 = 1L;
    private Long avspillergruppeId2 = 2L;
    private List<String> levendeIdenter;

    @Before
    public void setUp() throws IOException {
        levendeIdenter = new ArrayList<>();
        levendeIdenter.add("20044249945");
        levendeIdenter.add("20044249946");
        levendeIdenter.add("20044249947");
        levendeIdenter.add("20044249948");

        List<String> foedteIdenter = new ArrayList<>();
        foedteIdenter.add("20041751231");

        when(cacheService.hentFoedteIdenterCache(avspillergruppeId2)).thenReturn(foedteIdenter);

        Map<String, String> status = new HashMap<>();
        status.put(DATO_DO, "");
    }

    /**
     * Scenario:
     * Henter identer fra en tom gruppe
     */
    @Test
    public void hentMyndigeIdenterIGruppeIngenIdenterTest() {
        var identer = eksisterendeIdenterService.hentLevendeIdenter(2L, miljoe, 2, MINIMUM_ALDER).collectList().block();
        assertTrue(identer.isEmpty());
    }


    /**
     * Scenario:
     * Finn fødte identer
     */
    @Test
    public void finnFoedteIdenterTest() {
        var minAlder = 0;
        var maksAlder = 200;
        var foedte = eksisterendeIdenterService.finnFoedteIdenter(avspillergruppeId2, minAlder, maksAlder);
        assertEquals(1, foedte.size());
        assertThat(foedte, containsInAnyOrder(
                "20041751231"
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

        var navEnhetResponse = eksisterendeIdenterService.hentFnrMedNavKontor(miljoe, levendeIdenter);

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
        var jsonContent = Resources.getResource("FS03-FDNUMMER-KERNINFO-O.json");
        var jsonNode = new ObjectMapper().readTree(jsonContent);
        var fnr1 = "23048801390";
        List<String> identer = new ArrayList<>();
        identer.add(fnr1);

        when(cacheService.hentLevendeIdenterCache(avspillergruppeId1)).thenReturn(identer);
        when(tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, fnr1)).thenReturn(jsonNode);

        var fnrMedStatusQuo = eksisterendeIdenterService.hentGittAntallIdenterMedStatusQuo(avspillergruppeId1, miljoe, identer.size(), MIN_ALDER, MAX_ALDER);

        verify(cacheService).hentLevendeIdenterCache(avspillergruppeId1);
        verify(tpsStatusQuoService).getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, fnr1);

        assertThat(fnrMedStatusQuo.get(fnr1), equalTo(jsonNode));
    }

    @Test
    public void hentGittAntallIdenterMedKontonummerTest() throws IOException {
        var jsonContent = Resources.getResource("FS03-FDNUMMER-KERNINFO-O.json");
        var jsonNode = new ObjectMapper().readTree(jsonContent);
        var fnr1 = "23048801390";
        List<String> identer = new ArrayList<>();
        identer.add(fnr1);

        when(cacheService.hentLevendeIdenterCache(avspillergruppeId1)).thenReturn(identer);
        when(tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, fnr1)).thenReturn(jsonNode);

        List<KontoinfoResponse> fnrMedKontoinfo = eksisterendeIdenterService.hentGittAntallIdenterMedKononummerinfo(avspillergruppeId1, miljoe, identer.size(), MIN_ALDER, MAX_ALDER);

        verify(cacheService).hentLevendeIdenterCache(avspillergruppeId1);
        verify(tpsStatusQuoService).getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, fnr1);

        assertThat(fnrMedKontoinfo.get(0).getFnr(), equalTo(fnr1));
        assertThat(fnrMedKontoinfo.get(0).getKontonummer(), equalTo("20000723267"));
    }

    @Test
    public void shouldHenteAdresserPaaIdent() throws IOException {
        var jsonContent = Resources.getResource("FS03-FDNUMMER-KERNINFO-O.json");
        var jsonNode = new ObjectMapper().readTree(jsonContent);
        var fnr1 = "23048801390";
        var identer = new ArrayList<>(Collections.singleton(fnr1));

        when(tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, identer.get(0))).thenReturn(jsonNode);

        var adressePaaIdenter = eksisterendeIdenterService.hentAdressePaaIdenter(miljoe, identer);

        assertThat(adressePaaIdenter.get("23048801390").findValue("boAdresse1").asText(), equalTo("WALLDORFERSTRABE 1289"));
    }

    @Test
    public void shouldHentePersondataPaaIdent() throws IOException {
        var fnr = "12101816735";
        var miljoe = "t1";

        var jsonNode = new ObjectMapper().readTree(Resources.getResource("persondata/persondata.json"));

        when(tpsStatusQuoService.getInfoOnRoutineName(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonNode);
        var response = eksisterendeIdenterService.hentPersondata(fnr, miljoe);

        assertThat(response.getFnr(), equalTo(fnr));
        assertThat(response.getFornavn(), equalTo("USTABIL"));
        assertThat(response.getEtternavn(), equalTo("PARASOLL"));
        assertThat(response.getStatsborger(), equalTo("NORGE"));
    }

    @Test
    public void shouldHandleEmptyRelasjon() throws IOException {
        var fnr = "12090080405";
        var miljoe = "t1";

        var jsonNode = new ObjectMapper().readTree(Resources.getResource("relasjoner/tom_relasjon.json"));

        when(tpsStatusQuoService.getInfoOnRoutineName(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonNode);
        var response = eksisterendeIdenterService.hentRelasjoner(fnr, miljoe);

        assertThat(response.getFnr(), equalTo(fnr));
        assertThat(response.getRelasjoner().size(), is(0));
    }

    @Test
    public void shouldHandleSingleRelasjon() throws IOException {
        var fnr = "12090080405";
        var miljoe = "t1";

        var jsonNode = new ObjectMapper().readTree(Resources.getResource("relasjoner/relasjon.json"));

        when(tpsStatusQuoService.getInfoOnRoutineName(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonNode);
        var response = eksisterendeIdenterService.hentRelasjoner(fnr, miljoe);

        assertThat(response.getFnr(), equalTo(fnr));
        assertThat(response.getRelasjoner().size(), is(1));
        assertThat(response.getRelasjoner().get(0).getFnrRelasjon(), equalTo("12021790069"));
    }

    @Test
    public void shouldHandleMultipleRelasjoner() throws IOException {
        var fnr = "12090080405";
        var miljoe = "t1";

        var jsonNode = new ObjectMapper().readTree(Resources.getResource("relasjoner/relasjoner.json"));

        when(tpsStatusQuoService.getInfoOnRoutineName(anyString(), anyString(), anyString(), anyString())).thenReturn(jsonNode);
        var response = eksisterendeIdenterService.hentRelasjoner(fnr, miljoe);

        assertThat(response.getFnr(), equalTo(fnr));
        assertThat(response.getRelasjoner().size(), is(2));
        assertThat(response.getRelasjoner().get(0).getFnrRelasjon(), equalTo("12021790069"));
        assertThat(response.getRelasjoner().get(1).getFnrRelasjon(), equalTo("17120080318"));
    }

    @Test
    public void shouldHenteIdenterNotInTps() throws IOException {
        var avspillergruppeId = 123L;
        var fnr1 = "20092943861";
        var fnr2 = "12345678910";
        var identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        var jsonNode = new ObjectMapper().readTree(Resources.getResource("tpsStatus/tps_status.json"));

        when(tpsfFiltreringService.finnAlleIdenter(avspillergruppeId)).thenReturn(identer);
        when(tpsfConsumer.hentTpsStatusPaaIdenter(eq("A0"), eq(miljoe), anyList())).thenReturn(jsonNode);

        var identerIkkeITps = eksisterendeIdenterService.hentIdenterSomIkkeErITps(avspillergruppeId, miljoe);

        assertThat(identerIkkeITps, contains(fnr2));
        assertThat(identerIkkeITps, not(contains(fnr1)));
    }

    @Test
    public void shouldHenteIdenterSomKolliderer() throws IOException {
        var avspillergruppeId = 123L;
        var fnr1 = "20092943861";
        var fnr2 = "12345678910";
        var identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        var jsonNode = new ObjectMapper().readTree(Resources.getResource("tpsStatus/tps_kollisjon.json"));

        when(tpsfFiltreringService.finnAlleIdenter(avspillergruppeId)).thenReturn(identer);
        when(tpsfConsumer.hentTpsStatusPaaIdenter(eq("A2"), eq("q2"), anyList())).thenReturn(jsonNode);

        var identerSomKolliderer = eksisterendeIdenterService.hentIdenterSomKolliderer(avspillergruppeId);

        assertThat(identerSomKolliderer, contains(fnr1));
        assertThat(identerSomKolliderer, not(contains(fnr2)));
    }
}
