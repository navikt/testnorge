package no.nav.registre.hodejegeren.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EksisterendeIdenterServiceTest {

    @Mock
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;

    @Mock
    private Random rand;

    @InjectMocks
    private EksisterendeIdenterService eksisterendeIdenterService;

    private List<RsMeldingstype> meldinger;
    private List<String> identer;
    private List<String> brukteIdenter;
    private String environment;
    private Map<String, Integer> meldingerPerEndringskode;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private String fnr3 = "03030303030";

    @Before
    public void setUp() {
        meldinger = new ArrayList<>();
        meldinger.add(new RsMeldingstype1Felter());

        identer = new ArrayList<>();
        identer.add(fnr1);
        identer.add(fnr2);
        identer.add(fnr3);

        brukteIdenter = new ArrayList<>();

        environment = "t1";

        meldingerPerEndringskode = new HashMap<>();
    }

    /**
     * Testscenario: HVIS det skal opprettes melding for en årsak som ikke krever spesiell behandling, skal systemet
     * i metoden {@link EksisterendeIdenterService#behandleGenerellAarsak}, finne en person som er i live og norsk
     * statsborger. Systemet skal legge denne personen inn i listen av brukte identer.
     */
    @Test
    public void shouldFindLevendeNordmannAndUpdateBrukteIdenter() throws IOException {
        Endringskoder endringskode = Endringskoder.NAVNEENDRING_FOERSTE;
        meldingerPerEndringskode.put(endringskode.getEndringskode(), 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettLevendeNordmennMock();

        eksisterendeIdenterService.behandleGenerellAarsak(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(endringskodeTilFeltnavnMapperService, times(2)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(1, meldinger.size());
        assertEquals(fnr2.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato());

        assertEquals(1, brukteIdenter.size());
        assertEquals(fnr2, brukteIdenter.get(0));
    }

    /**
     * Testscenario: HVIS status quo på en ident inneholder felter uten verdi eller med null-verdier på felter som
     * kreves i metoden, skal systemet velge en annen ident.
     */
    @Test
    public void shouldHandleIdenterWithNullValuesInStatusQuoFields() throws IOException {
        Endringskoder endringskode = Endringskoder.NAVNEENDRING_FOERSTE;

        meldingerPerEndringskode.put(endringskode.getEndringskode(), 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettIdenterMedManglendeFeltMock();

        eksisterendeIdenterService.behandleGenerellAarsak(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(endringskodeTilFeltnavnMapperService, times(3)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(1, meldinger.size());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato());
    }

    /**
     * Testscenario: HVIS det skal opprettes vigselsmelding, skal systemet i metoden {@link EksisterendeIdenterService#behandleVigsel},
     * finne to personer som er ugifte, og myndige, og legge vigselsmelding på disse, og påse at hver av identene legges inn
     * som relasjon til den andre.
     */
    @Test
    public void shouldFindUgiftMyndigPersonAndCreateVigselsmelding() throws IOException {
        Endringskoder endringskode = Endringskoder.VIGSEL;
        meldingerPerEndringskode.put(endringskode.getEndringskode(), 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleUgifteIdenterMock();

        eksisterendeIdenterService.behandleVigsel(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(endringskodeTilFeltnavnMapperService, times(4)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(2, meldinger.size());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerFdato());
        assertEquals(fnr1.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerFdato());
    }


    /**
     * Testscenario: HVIS det skal opprettes skilsmisse-/seperasjonsmelding, skal systemet i metoden
     * {@link EksisterendeIdenterService#behandleSeperasjonSkilsmisse}, finne en gift person, og legge
     * skilsmisse-/seperasjonsmelding på denne, og påse at tilsvarende melding legges på partner.
     */
    @Test
    public void shouldFindGiftPersonAndCreateSkilsmissemelding() throws IOException {
        Endringskoder endringskode = Endringskoder.SKILSMISSE;
        meldingerPerEndringskode.put(endringskode.getEndringskode(), 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleGifteIdenterMock();

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(endringskodeTilFeltnavnMapperService, times(3)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(2, meldinger.size());
    }

    /**
     * Testscenario: HVIS det skal opprettes dødsmelding, skal systemet i metoden {@link EksisterendeIdenterService#behandleDoedsmelding},
     * finne en levende norsk statsborger, og legge dødsmelding på denne. Systemet skal i tilfeller der personen er gift,
     * sette partner til enke/enkemann.
     */
    @Test
    public void shouldFindPartnerOfDoedsmeldingIdentAndCreateSivilstandendringsmelding() throws IOException {
        Endringskoder endringskode = Endringskoder.DOEDSMELDING;
        meldingerPerEndringskode.put(endringskode.getEndringskode(), 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettEkteparMock();

        eksisterendeIdenterService.behandleDoedsmelding(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(endringskodeTilFeltnavnMapperService, times(2)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKode(), ((RsMeldingstype1Felter) meldinger.get(1)).getSivilstand());
    }

    /**
     * Testscenario: HVIS det skal opprettes en vigselsmelding og det er for få ledige identer tilgjengelig, skal det
     * skrives ut en melding til loggen.
     */
    @Test
    public void shouldLogWarningForTooFewIdents() {
        Endringskoder endringskode = Endringskoder.VIGSEL;

        Logger logger = (Logger) LoggerFactory.getLogger(EksisterendeIdenterService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        eksisterendeIdenterService.behandleVigsel(meldinger, Arrays.asList("01010101010"), brukteIdenter, endringskode, environment);

        assertEquals(1, listAppender.list.size());
        assertTrue(listAppender.list.get(0).toString().contains("Kunne ikke finne ident for SkdMelding med meldingsnummer"));
    }

    @Test
    public void shouldGetFoedselsdatoFromFnr() {
        String fnr1 = "14041212345";
        String fnr2 = "14041254321";
        assertEquals(LocalDate.of(1912, 4, 14), eksisterendeIdenterService.getFoedselsdatoFraFnr(fnr1));
        assertEquals(LocalDate.of(2012, 4, 14), eksisterendeIdenterService.getFoedselsdatoFraFnr(fnr2));
    }

    private void opprettLevendeNordmennMock() throws IOException {
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(DATO_DO, "010203");
        statusQuo.put(STATSBORGER, "NORGE");
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), any(), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(DATO_DO, "");
        statusQuo.put(STATSBORGER, "NORGE");
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), any(), eq(fnr2))).thenReturn(statusQuo);
    }

    private void opprettIdenterMedManglendeFeltMock() throws IOException {
        Map<String, String> statusQuo = new HashMap<>();
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), any(), eq(fnr1))).thenReturn(statusQuo);

        statusQuo.put(DATO_DO, "010203");
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), any(), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), any(), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettMultipleUgifteIdenterMock() throws IOException {
        LocalDate fodselsdato = LocalDate.now();
        String year = String.valueOf(fodselsdato.getYear()).substring(2);

        String fnrUmyndig = "1010" + year + "51010";
        identer.add(0, fnrUmyndig);

        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnrUmyndig))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettMultipleGifteIdenterMock() throws IOException {
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, fnr3);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettEkteparMock() throws IOException {
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        statusQuo.put(DATO_DO, "");
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        statusQuo.put(DATO_DO, "");
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, fnr1);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr2))).thenReturn(statusQuo);
    }
}
