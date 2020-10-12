package no.nav.registre.skd.service;

import static no.nav.registre.skd.service.EksisterendeIdenterService.DATO_DO;
import static no.nav.registre.skd.service.EksisterendeIdenterService.FNR_RELASJON;
import static no.nav.registre.skd.service.EksisterendeIdenterService.RELASJON_MOR;
import static no.nav.registre.skd.service.EksisterendeIdenterService.SIVILSTAND;
import static no.nav.registre.skd.service.EksisterendeIdenterService.STATSBORGER;
import static no.nav.registre.skd.service.Endringskoder.FARSKAP_MEDMORSKAP;
import static no.nav.registre.skd.service.Endringskoder.SKILSMISSE;
import static no.nav.registre.skd.service.KoderForSivilstand.GIFT;
import static no.nav.registre.skd.service.KoderForSivilstand.SKILT;
import static no.nav.registre.skd.testutils.Utils.testLoggingInClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.HodejegerenConsumerSkd;
import no.nav.registre.skd.consumer.dto.Relasjon;
import no.nav.registre.skd.consumer.response.RelasjonsResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.skd.exceptions.ManglendeInfoITpsException;
import no.nav.registre.skd.exceptions.ManglerEksisterendeIdentException;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class EksisterendeIdenterServiceTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private HodejegerenConsumerSkd hodejegerenConsumerSkd;

    @Mock
    private FoedselService foedselService;

    @Mock
    private Random rand;

    @InjectMocks
    private EksisterendeIdenterService eksisterendeIdenterService;

    private List<RsMeldingstype> meldinger;
    private List<String> identer;
    private List<String> brukteIdenter;
    private String environment;
    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private final String fnr3 = "03030303030";
    private final String fnr4 = "04040404040";
    private final String fnr5 = "05050505050";

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
    public void shouldFindLevendeNordmannAndUpdateBrukteIdenter() {
        var endringskode = Endringskoder.NAVNEENDRING_FOERSTE;

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettLevendeNordmennMock();

        eksisterendeIdenterService.behandleGenerellAarsak(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(hodejegerenConsumerSkd, times(2)).getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), anyString());
        assertEquals(1, meldinger.size());
        assertEquals(fnr2, ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer());

        assertEquals(1, brukteIdenter.size());
        assertEquals(fnr2, brukteIdenter.get(0));
    }

    /**
     * Testscenario: HVIS status quo på en ident inneholder felter uten verdi eller med null-verdier på felter som kreves i metoden,
     * skal systemet velge en annen ident.
     */
    @Test
    public void shouldHandleIdenterWithNullValuesInStatusQuoFields() {
        var endringskode = Endringskoder.NAVNEENDRING_FOERSTE;

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettIdenterMedManglendeFeltMock();

        eksisterendeIdenterService.behandleGenerellAarsak(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(hodejegerenConsumerSkd, times(3)).getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), anyString());
        assertEquals(1, meldinger.size());
        assertEquals(fnr3, ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer());
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
    public void shouldFindUgiftMyndigPersonAndCreateVigselsmelding() {
        var endringskode = Endringskoder.VIGSEL;

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleUgifteIdenterMock(); // fnr1,2 og 3 er myndige. fnr2 er gift. Resten er ugift.

        eksisterendeIdenterService.behandleVigsel(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(hodejegerenConsumerSkd, times(4)).getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), anyString());
        assertEquals(2, meldinger.size());
        assertEquals(fnr1, ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer());
        assertEquals(fnr3, ((RsMeldingstype1Felter) meldinger.get(1)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(1)).getPersonnummer());
        assertEquals(fnr3, ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerFdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerPnr());
        assertEquals(fnr1, ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerFdato() + ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerPnr());
        assertEquals(GIFT.getSivilstandKodeSKD(), ((RsMeldingstype1Felter) meldinger.get(0)).getSivilstand());
        assertEquals(GIFT.getSivilstandKodeSKD(), ((RsMeldingstype1Felter) meldinger.get(1)).getSivilstand());
    }

    /**
     * HVIS et ekte-/samboer-par skal legges til i skilsmisse/separasjonsmelding, MEN deres status quo i TPS ikke stemmer overens
     * (ulik sivilstand eller ikke registrert gjensidig relasjon), SÅ skal avviket loggføres og et nytt ekte-/samboer-par blir fylt
     * inn i meldingen i stedet.
     */
    @Test
    public void shouldLogAndRetryWithNewCoupleIfTheirDataIsCorrupt() {
        ListAppender<ILoggingEvent> listAppender = testLoggingInClass(EksisterendeIdenterService.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        var identerIRekkefoelge = opprettEkteparMedKorruptDataMock(); // Første paret har data som feiler. Andre paret er fungerende og vanlig.

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identerIRekkefoelge, brukteIdenter, SKILSMISSE, environment);

        assertEquals(1, listAppender.list.size());
        assertTrue(listAppender.list.get(0).toString().contains("Korrupte data i TPS - personnummeret eller sivilstanden stemmer ikke for personene med fødselsnumrene: "
                + fnr1 + " og " + fnr2));
        assertEquals(fnr3, ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer());
    }

    private List<String> opprettEkteparMedKorruptDataMock() {
        // oppretter ektepar med korrupt data på nummer 2 av ektefellene.
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        // fnr2 er feilregistrert i TPS til ugift sivilstand.
        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr5);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr2))).thenReturn(statusQuo);

        // Et fungerende ektepar
        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr3);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr4))).thenReturn(statusQuo);
        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr4);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr3))).thenReturn(statusQuo);
        return new ArrayList(Arrays.asList(fnr1, fnr2, fnr3, fnr4));
    }

    /**
     * Testscenario: HVIS syntetisk skilsmisse-/seperasjonsmelding behandles, skal metoden
     * {@link EksisterendeIdenterService#behandleSeperasjonSkilsmisse} finne en gift person og legge identifiserende informasjon for
     * personen og personens partner på skilsmisse-/seperasjonsmeldingen. Deretter skal metoden opprette en tilsvarende
     * skilsmisse-/seperasjonsmelding for partneren.
     */
    @Test
    public void shouldFindGiftPersonAndCreateSkilsmissemelding() {
        meldinger.get(0).setAarsakskode(SKILSMISSE.getAarsakskode());
        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleGifteIdenterMock();

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identer, brukteIdenter, SKILSMISSE, environment);

        verify(hodejegerenConsumerSkd, times(3)).getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), anyString());
        assertEquals(2, meldinger.size());
        assertEquals(SKILSMISSE.getAarsakskode(), meldinger.get(0).getAarsakskode());
        assertEquals(fnr2, ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer());
        assertEquals(SKILSMISSE.getAarsakskode(), meldinger.get(1).getAarsakskode());
        assertEquals(fnr3, ((RsMeldingstype1Felter) meldinger.get(1)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(1)).getPersonnummer());
        assertEquals(fnr3, ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerFdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerPnr());
        assertEquals(SKILT.getSivilstandKodeSKD(), ((RsMeldingstype1Felter) meldinger.get(1)).getSivilstand());
    }

    @Test
    public void shouldFindFarForFarskapsmeldingAndRemoveInvalidMeldinger() {
        var foedteIdenter = new ArrayList<>(Collections.singletonList(
                "01011953456"
        ));

        var levendeIdenter = new ArrayList<>(Arrays.asList(
                "01029153465",
                "02058762345"
        ));

        meldinger.add(new RsMeldingstype1Felter());
        meldinger.get(0).setAarsakskode(FARSKAP_MEDMORSKAP.getAarsakskode());

        var relasjon = Relasjon.builder()
                .typeRelasjon(RELASJON_MOR)
                .fnrRelasjon(levendeIdenter.get(0))
                .build();
        var relasjonsResponse = RelasjonsResponse.builder()
                .fnr(foedteIdenter.get(0))
                .relasjoner(new ArrayList<>(Collections.singletonList(relasjon)))
                .build();
        when(hodejegerenConsumerSkd.getRelasjoner(foedteIdenter.get(0), environment)).thenReturn(relasjonsResponse);

        when(foedselService.findFar(eq(levendeIdenter.get(0)), eq(foedteIdenter.get(0)), eq(levendeIdenter), anyList())).thenReturn(levendeIdenter.get(1));

        eksisterendeIdenterService.behandleFarskapMedmorskap(meldinger, levendeIdenter, foedteIdenter, brukteIdenter, FARSKAP_MEDMORSKAP, environment);

        verify(hodejegerenConsumerSkd).getRelasjoner(foedteIdenter.get(0), environment);
        verify(foedselService).findFar(eq(levendeIdenter.get(0)), eq(foedteIdenter.get(0)), eq(levendeIdenter), anyList());
        assertThat(((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato(), equalTo(foedteIdenter.get(0).substring(0, 6)));
        assertThat(((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer(), equalTo(foedteIdenter.get(0).substring(6)));
        assertThat(((RsMeldingstype1Felter) meldinger.get(0)).getFarsFodselsdato(), equalTo(levendeIdenter.get(1).substring(0, 6)));
        assertThat(((RsMeldingstype1Felter) meldinger.get(0)).getFarsPersonnummer(), equalTo(levendeIdenter.get(1).substring(6)));
        assertThat(meldinger.size(), equalTo(1));
    }

    /**
     * Testscenario: HVIS metoden {@link HodejegerenConsumerSkd#getStatusQuoTilhoerendeEndringskode(String, String, String)} kaster
     * {@link ManglendeInfoITpsException} ved behandling av en ident, skal metoden
     * findExistingPersonStatusInTps prøve å finne en ny ident, og be om status quo på denne.
     */
    @Test()
    public void shouldFindNewPersonWhenEncounteringStatusQuoError() {
        meldinger.get(0).setAarsakskode(SKILSMISSE.getAarsakskode());
        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleGifteIdenterWithStatusQuoErrorMock();

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identer, brukteIdenter, SKILSMISSE, environment);

        verify(hodejegerenConsumerSkd, times(4)).getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), anyString());
        assertEquals(2, meldinger.size());
        assertEquals(SKILSMISSE.getAarsakskode(), meldinger.get(0).getAarsakskode());
        assertEquals(fnr3, ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer());
        assertEquals(SKILSMISSE.getAarsakskode(), meldinger.get(1).getAarsakskode());
        assertEquals(fnr4, ((RsMeldingstype1Felter) meldinger.get(1)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(1)).getPersonnummer());
        assertEquals(fnr4, ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerFdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerPnr());
        assertEquals(fnr3, ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerFdato() + ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerPnr());

    }

    /**
     * Testscenario: HVIS dødsmelding skal behandles av Hodejegeren, skal systemet i metoden
     * {@link EksisterendeIdenterService#behandleDoedsmelding}, finne en levende norsk statsborger, og legge dødsmelding på denne.
     * <p>
     * Når dødsmelding registreres på en gift person, så vil Hodejegeren opprette en endringsmelding på sivilstand for å
     * omregistrere ektefellen til enke/enkemann.
     */
    @Test
    public void shouldFindPartnerOfDoedsmeldingIdentAndCreateSivilstandendringsmelding() {
        var endringskode = Endringskoder.DOEDSMELDING;

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettEkteparMock();

        eksisterendeIdenterService.behandleDoedsmelding(meldinger, identer, brukteIdenter, endringskode, environment);

        verify(hodejegerenConsumerSkd, times(2)).getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), anyString());
        assertEquals(2, meldinger.size());
        assertEquals("8510", meldinger.get(1).getAarsakskode() + ((RsMeldingstype1Felter) meldinger.get(1)).getStatuskode() + ((RsMeldingstype1Felter) meldinger.get(1)).getTildelingskode());
        assertEquals(fnr1, ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(0)).getPersonnummer());
        assertEquals(fnr2, ((RsMeldingstype1Felter) meldinger.get(1)).getFodselsdato() + ((RsMeldingstype1Felter) meldinger.get(1)).getPersonnummer());
        assertEquals(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKodeSKD(), ((RsMeldingstype1Felter) meldinger.get(1)).getSivilstand());
    }

    /**
     * Testscenario: HVIS det skal opprettes en vigselsmelding og det er for få ledige identer tilgjengelig, skal det kastes en
     * exception med beskrivende feilmelding som inneholder meldingsnummeret til meldingen som feilet.
     */
    @Test
    public void shouldThrowExceptionForTooFewIdents() {
        var meldingsnummer = "123";

        expectedException.expect(ManglerEksisterendeIdentException.class);
        expectedException.expectMessage("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                + meldingsnummer + ". For få identer i listen singleIdenterINorge fra TPSF avspillergruppen.");

        var endringskode = Endringskoder.VIGSEL;

        meldinger.get(0).setMeldingsnrHosTpsSynt(meldingsnummer);

        eksisterendeIdenterService.behandleVigsel(meldinger, Collections.singletonList("01010101010"), brukteIdenter, endringskode, environment);
    }

    private void opprettLevendeNordmennMock() {
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(DATO_DO, "010203");
        statusQuo.put(STATSBORGER, "NORGE");
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(DATO_DO, "");
        statusQuo.put(STATSBORGER, "NORGE");
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), eq(fnr2))).thenReturn(statusQuo);
    }

    private void opprettIdenterMedManglendeFeltMock() {
        Map<String, String> statusQuo = new HashMap<>();
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), eq(fnr1))).thenReturn(statusQuo);

        statusQuo.put(DATO_DO, "010203");
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), anyString(), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettMultipleUgifteIdenterMock() {
        var fodselsdato = LocalDate.now();
        var year = String.valueOf(fodselsdato.getYear()).substring(2);

        var fnrUmyndig = "1010" + year + "51010";
        identer.add(0, fnrUmyndig);

        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnrUmyndig))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettMultipleGifteIdenterMock() {
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr3);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettMultipleGifteIdenterWithStatusQuoErrorMock() {
        identer.add(fnr4);

        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKodeSKD());
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr2))).thenThrow(new ManglendeInfoITpsException());

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr4);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr3))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr3);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr4))).thenReturn(statusQuo);
    }

    private void opprettEkteparMock() {
        Map<String, String> statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        statusQuo.put(DATO_DO, "");
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        statusQuo.put(DATO_DO, "");
        statusQuo.put(SIVILSTAND, GIFT.getSivilstandKodeSKD());
        statusQuo.put(FNR_RELASJON, fnr1);
        when(hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(anyString(), eq(environment), eq(fnr2))).thenReturn(statusQuo);
    }
}
