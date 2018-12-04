package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.FNR_RELASJON;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.SIVILSTAND;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static no.nav.registre.hodejegeren.service.Endringskoder.SKILSMISSE;
import static no.nav.registre.hodejegeren.testutils.Utils.testLoggingInClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;
import no.nav.registre.hodejegeren.exception.ManglerEksisterendeIdentException;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class EksisterendeIdenterServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
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
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private String fnr3 = "03030303030";
    private String fnr4 = "04040404040";
    private String fnr5 = "05050505050";

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

    }

    /**
     * Testscenario: HVIS det skal opprettes melding for en årsak som ikke krever spesiell behandling, skal systemet i metoden
     * {@link EksisterendeIdenterService#behandleGenerellAarsak}, finne en person som er i live og norsk statsborger. Systemet skal
     * legge denne personen inn i listen av brukte identer.
     */
    @Test
    public void shouldFindLevendeNordmannAndUpdateBrukteIdenter() throws IOException {
        Endringskoder endringskode = Endringskoder.NAVNEENDRING_FOERSTE;

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
     * Testscenario: HVIS status quo på en ident inneholder felter uten verdi eller med null-verdier på felter som kreves i metoden,
     * skal systemet velge en annen ident.
     */
    @Test
    public void shouldHandleIdenterWithNullValuesInStatusQuoFields() throws IOException {
        Endringskoder endringskode = Endringskoder.NAVNEENDRING_FOERSTE;

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettIdenterMedManglendeFeltMock();

        eksisterendeIdenterService.behandleGenerellAarsak(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(endringskodeTilFeltnavnMapperService, times(3)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(1, meldinger.size());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato());
    }

    /**
     * Testscenario: HVIS det skal opprettes vigselsmelding, skal systemet i metoden
     * {@link EksisterendeIdenterService#behandleVigsel}, finne to personer som er ugifte og myndige, og legge vigselsmelding på
     * disse, og påse at hver av identene legges inn som relasjon til den andre. Det opprettes en vigselsmelding for hver av
     * personene.
     * <p>
     * Personer må være minst 18 år (myndige) for å kunne settes på en vigselsmelding.
     */
    @Test
    public void shouldFindUgiftMyndigPersonAndCreateVigselsmelding() throws IOException {
        Endringskoder endringskode = Endringskoder.VIGSEL;

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleUgifteIdenterMock(); // fnr1,2 og 3 er myndige. fnr2 er gift. Resten er ugift.

        eksisterendeIdenterService.behandleVigsel(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(endringskodeTilFeltnavnMapperService, times(4)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(2, meldinger.size());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerFdato());
        assertEquals(fnr1.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerFdato());
    }

    /**
     * HVIS et ekte-/samboer-par skal legges til i skilsmisse/separasjonsmelding, MEN deres status quo i TPS ikke stemmer overens
     * (ulik sivilstand eller ikke registrert gjensidig relasjon), SÅ skal avviket loggføres og et nytt ekte-/samboer-par blir fylt
     * inn i meldingen i stedet.
     */
    @Test
    public void shouldLogAndRetryWithNewCoupleIfTheirDataIsCorrupt() throws IOException {
        ListAppender<ILoggingEvent> listAppender = testLoggingInClass(EksisterendeIdenterService.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        List<String> identerIRekkefølge = opprettEkteparMedKorruptDataMock(); // Første paret har data som feiler. Andre paret er fungerende og vanlig.

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identerIRekkefølge, brukteIdenter, SKILSMISSE, environment);

        assertEquals(1, listAppender.list.size());
        assertTrue(listAppender.list.get(0).toString().contains("Korrupte data i TPS - personnummeret eller sivilstanden stemmer ikke for personene med fødselsnumrene: "
                + fnr1 + " og " + fnr2));
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato());
    }

    private List<String> opprettEkteparMedKorruptDataMock() throws IOException {
        // oppretter ektepar med korrupt data på nummer 2 av ektefellene.
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        // fnr2 er feilregistrert i TPS til ugift sivilstand.
        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr5);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr2))).thenReturn(statusQuo);

        // Et fungerende ektepar
        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr3);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr4))).thenReturn(statusQuo);
        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr4);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr3))).thenReturn(statusQuo);
        return new ArrayList(Arrays.asList(fnr1, fnr2, fnr3, fnr4));
    }

    /**
     * Testscenario: HVIS syntetisk skilsmisse-/seperasjonsmelding behandles, skal metoden
     * {@link EksisterendeIdenterService#behandleSeperasjonSkilsmisse} finne en gift person og legge identifiserende informasjon for
     * personen og personens partner på skilsmisse-/seperasjonsmeldingen. Deretter skal metoden opprette en tilsvarende
     * skilsmisse-/seperasjonsmelding for partneren.
     */
    @Test
    public void shouldFindGiftPersonAndCreateSkilsmissemelding() throws IOException {
        meldinger.get(0).setAarsakskode(SKILSMISSE.getAarsakskode());
        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleGifteIdenterMock();

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identer, brukteIdenter, SKILSMISSE, environment);

        verify(endringskodeTilFeltnavnMapperService, times(3)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(2, meldinger.size());
        assertEquals(SKILSMISSE.getAarsakskode(), meldinger.get(0).getAarsakskode());
        assertEquals(fnr2.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato());
        assertEquals(SKILSMISSE.getAarsakskode(), meldinger.get(1).getAarsakskode());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(1)).getFodselsdato());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerFdato());
        assertEquals(fnr2.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerFdato());
    }

    /**
     * Testscenario: HVIS metoden {@link EndringskodeTilFeltnavnMapperService#getStatusQuoFraAarsakskode} kaster
     * {@link ManglendeInfoITpsException} ved behandling av en ident, skal metoden
     * {@link EksisterendeIdenterService#checkValidStatusQuo} prøve å finne en ny ident, og be om status quo på denne. Antall forsøk
     * er angitt i {@link EksisterendeIdenterService#ANTALL_FORSOEK_PER_AARSAK}
     */
    @Test()
    public void shouldFindNewPersonWhenEncounteringStatusQuoError() throws IOException {
        meldinger.get(0).setAarsakskode(SKILSMISSE.getAarsakskode());
        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleGifteIdenterWithStatusQuoErrorMock();

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identer, brukteIdenter, SKILSMISSE, environment);

        verify(endringskodeTilFeltnavnMapperService, times(4)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(2, meldinger.size());
        assertEquals(SKILSMISSE.getAarsakskode(), meldinger.get(0).getAarsakskode());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato());
        assertEquals(SKILSMISSE.getAarsakskode(), meldinger.get(1).getAarsakskode());
        assertEquals(fnr4.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(1)).getFodselsdato());
        assertEquals(fnr4.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerFdato());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerFdato());

    }

    /**
     * Testscenario: HVIS dødsmelding skal behandles av Hodejegeren, skal systemet i metoden
     * {@link EksisterendeIdenterService#behandleDoedsmelding}, finne en levende norsk statsborger, og legge dødsmelding på denne.
     * <p>
     * Når dødsmelding registreres på en gift person, så vil Hodejegeren opprette en endringsmelding på sivilstand for å
     * omregistrere ektefellen til enke/enkemann.
     */
    @Test
    public void shouldFindPartnerOfDoedsmeldingIdentAndCreateSivilstandendringsmelding() throws IOException {
        Endringskoder endringskode = Endringskoder.DOEDSMELDING;

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettEkteparMock();

        eksisterendeIdenterService.behandleDoedsmelding(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(endringskodeTilFeltnavnMapperService, times(2)).getStatusQuoFraAarsakskode(any(), any(), any());
        assertEquals(2, meldinger.size());
        assertEquals(fnr1.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato());
        assertEquals(fnr2.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(1)).getFodselsdato());
        assertEquals(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKodeSKD(), ((RsMeldingstype1Felter) meldinger.get(1)).getSivilstand());
    }

    /**
     * Testscenario: HVIS det skal opprettes en vigselsmelding og det er for få ledige identer tilgjengelig, skal det kastes en
     * exception med beskrivende feilmelding som inneholder meldingsnummeret til meldingen som feilet.
     */
    @Test
    public void shouldThrowExceptionForTooFewIdents() {
        String meldingsnummer = "123";

        expectedException.expect(ManglerEksisterendeIdentException.class);
        expectedException.expectMessage("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                + meldingsnummer + ". For få identer i listen singleIdenterINorge fra TPSF avspillergruppen.");

        Endringskoder endringskode = Endringskoder.VIGSEL;

        meldinger.get(0).setMeldingsnrHosTpsSynt(meldingsnummer);

        eksisterendeIdenterService.behandleVigsel(meldinger, Arrays.asList("01010101010"), brukteIdenter, endringskode, environment);
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
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnrUmyndig))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettMultipleGifteIdenterMock() throws IOException {
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr3);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettMultipleGifteIdenterWithStatusQuoErrorMock() throws IOException {
        identer.add(fnr4);

        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr2))).thenThrow(new ManglendeInfoITpsException());

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr4);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr3))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr3);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr4))).thenReturn(statusQuo);
    }

    private void opprettEkteparMock() throws IOException {
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        statusQuo.put(DATO_DO, "");
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        statusQuo.put(DATO_DO, "");
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr1);
        when(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(environment), eq(fnr2))).thenReturn(statusQuo);
    }
}
