package no.nav.registre.bisys.service;

import static no.nav.registre.bisys.service.SyntetiseringService.RELASJON_FAR;
import static no.nav.registre.bisys.service.SyntetiseringService.RELASJON_MOR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknFraConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknTypeConstants;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.ui.BisysUiConsumer;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.exception.SyntetisertBidragsmeldingException;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.Relasjon;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.RelasjonsResponse;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class SyntetiseringServiceTest {

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private BisysSyntetisererenConsumer bisysSyntetisererenConsumer;

    @Mock
    private BisysUiConsumer bisysUiConsumer;

    private SyntetiseringService syntetiseringService;

    private Long avspillergruppeId = 123L;
    private String env = "t1";
    private SyntetiserBisysRequest syntetiserBisysRequest;
    private List<String> foedteIdenter;
    private String barn1 = "04041956789";
    private String barn2 = "03051712345";
    private String barnUnborn = "33333";
    private String barn1YO = "22222";
    private String barn3YO = "62515";
    private String barnCloseTo18YO = "63121";
    private String barn18YO = "11111";
    private String barn21YO = "55113";

    private String barn12YOAtMinMottattdato = "19955";
    private String barn16YOAtMinMottattdato = "30300";
    private String barn21AtMinMottattdato = "19880";
    private String bidragsmottaker = "01016259875";
    private String bidragspliktig = "02056157925";
    private List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger;
    private List<Relasjon> relasjoner;

    @Before
    public void setUp() {
        barnUnborn = LocalDate.now().plusMonths(3).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barnUnborn;
        barn1YO = LocalDate.now().minusYears(1).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barn1YO;
        barn3YO = LocalDate.now().minusYears(3).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barn3YO;
        barnCloseTo18YO = LocalDate.now().minusYears(18).plusMonths(5).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barnCloseTo18YO;
        barn18YO = LocalDate.now().minusYears(18).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barn18YO;
        barn21YO = LocalDate.now().minusYears(21).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barn21YO;

        barn12YOAtMinMottattdato = SyntetiseringService.MIN_MOTTATT_DATO.minusYears(12).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barn12YOAtMinMottattdato;
        barn16YOAtMinMottattdato = SyntetiseringService.MIN_MOTTATT_DATO.minusYears(16).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barn16YOAtMinMottattdato;
        barn21AtMinMottattdato = SyntetiseringService.MIN_MOTTATT_DATO.minusYears(21).toString(DateTimeFormat.forPattern(("ddMMyy"))) + barn21AtMinMottattdato;

        syntetiseringService = syntetiseringServiceBuilder(false);

        foedteIdenter = new ArrayList<>(Arrays.asList(barn1, barn2));

        syntetiserBisysRequest = new SyntetiserBisysRequest(avspillergruppeId, env, foedteIdenter.size());
        syntetiserteBidragsmeldinger = new ArrayList<>(
                Arrays.asList(
                        SyntetisertBidragsmelding.builder()
                                .soknadstype(KodeSoknTypeConstants.SOKNAD)
                                .soknadFra(KodeSoknFraConstants.BM)
                                .soktFra("3")
                                .soktOm(KodeSoknGrKomConstants.BIDRAG_INNKREVING).build(),

                        SyntetisertBidragsmelding.builder()
                                .soknadstype(KodeSoknTypeConstants.SOKNAD)
                                .soknadFra(KodeSoknFraConstants.BM)
                                .soktFra("6")
                                .soktOm(KodeSoknGrKomConstants.BIDRAG_INNKREVING).build()));

        relasjoner = new ArrayList<>(
                Arrays.asList(
                        Relasjon.builder().fnrRelasjon(bidragsmottaker).typeRelasjon(RELASJON_MOR).build(),
                        Relasjon.builder().fnrRelasjon(bidragspliktig).typeRelasjon(RELASJON_FAR).build()));
    }

    private SyntetiseringService syntetiseringServiceBuilder(boolean useHistorical) {
        return SyntetiseringService.builder()
                .hodejegerenConsumer(hodejegerenConsumer)
                .bisysSyntetisererenConsumer(bisysSyntetisererenConsumer)
                .bisysUiConsumer(bisysUiConsumer)
                .useHistoricalMottattdato(useHistorical).build();
    }

    @Test
    public void shouldGenerateBidragsmeldinger() throws SyntetisertBidragsmeldingException {
        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(foedteIdenter);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(foedteIdenter.size()))
                .thenReturn(syntetiserteBidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(barn1, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn1).relasjoner(relasjoner).build());

        when(hodejegerenConsumer.getRelasjoner(barn2, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn2).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger = syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);

        assertThat(syntetiserteBidragsmeldinger.get(0).getBarn(), equalTo(barn1));
        assertThat(syntetiserteBidragsmeldinger.get(0).getBidragsmottaker(), equalTo(bidragsmottaker));
        assertThat(syntetiserteBidragsmeldinger.get(0).getBidragspliktig(), equalTo(bidragspliktig));
        assertThat(syntetiserteBidragsmeldinger.get(1).getBarn(), equalTo(barn2));
        assertThat(syntetiserteBidragsmeldinger.get(1).getBidragsmottaker(), equalTo(bidragsmottaker));
        assertThat(syntetiserteBidragsmeldinger.get(1).getBidragspliktig(), equalTo(bidragspliktig));
    }

    @Test
    public void shouldLogOnTooFewIdenter() throws SyntetisertBidragsmeldingException {
        Logger logger = (Logger) LoggerFactory.getLogger(SyntetiseringService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        foedteIdenter.remove(foedteIdenter.size() - 1);
        syntetiserteBidragsmeldinger.remove(syntetiserteBidragsmeldinger.size() - 1);
        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(foedteIdenter);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(foedteIdenter.size()))
                .thenReturn(syntetiserteBidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(barn1, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn1).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger = syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);

        assertThat(syntetiserteBidragsmeldinger.get(0).getBarn(), equalTo(barn1));
        assertThat(syntetiserteBidragsmeldinger.get(0).getBidragsmottaker(), equalTo(bidragsmottaker));
        assertThat(syntetiserteBidragsmeldinger.get(0).getBidragspliktig(), equalTo(bidragspliktig));
        assertThat(listAppender.list.size(), is(CoreMatchers.equalTo(1)));
        assertThat(
                listAppender.list.get(0).toString(),
                containsString(
                        "Fant ikke nok identer registrert med mor og far. Oppretter 1 bidragsmelding(er)."));
    }

    @Test
    public void shouldProcessBidragsmeldingerIfNoExceptionsOccur()
            throws BidragRequestProcessingException, SyntetisertBidragsmeldingException {
        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(foedteIdenter);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(foedteIdenter.size()))
                .thenReturn(syntetiserteBidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(barn1, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn1).relasjoner(relasjoner).build());

        when(hodejegerenConsumer.getRelasjoner(barn2, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn2).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger = syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);
        for (SyntetisertBidragsmelding bidragsmelding : syntetiserteBidragsmeldinger) {
            doNothing().when(bisysUiConsumer).createVedtak(bidragsmelding);
        }

        syntetiseringService.processBidragsmeldinger(syntetiserteBidragsmeldinger);
    }

    @Test
    public void skipToNextBidragsmeldingIfExceptionOccur()
            throws BidragRequestProcessingException, SyntetisertBidragsmeldingException {
        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(foedteIdenter);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(foedteIdenter.size()))
                .thenReturn(syntetiserteBidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(barn1, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn1).relasjoner(relasjoner).build());

        when(hodejegerenConsumer.getRelasjoner(barn2, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn2).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger = syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);
        for (SyntetisertBidragsmelding bidragsmelding : syntetiserteBidragsmeldinger) {
            if (bidragsmelding.getBarn().equals(barn1))
                doThrow(BidragRequestProcessingException.class)
                        .when(bisysUiConsumer)
                        .createVedtak(bidragsmelding);
            else
                doNothing().when(bisysUiConsumer).createVedtak(bidragsmelding);
        }

        syntetiseringService.processBidragsmeldinger(syntetiserteBidragsmeldinger);
    }

    @Test
    public void replaceBarnWith18YearOldWhenNecessaryAndHistorical() throws SyntetisertBidragsmeldingException {
        boolean useHistoricalMottattdato = true;
        replaceBarnWith18YearOldWhenNecessary(barn3YO, barn16YOAtMinMottattdato, useHistoricalMottattdato);
    }

    @Test
    public void replaceBarnWith18YearOldWhenNecessaryAndNotHistorical() throws SyntetisertBidragsmeldingException {
        boolean useHistoricalMottattdato = false;
        replaceBarnWith18YearOldWhenNecessary(barn3YO, barnCloseTo18YO, useHistoricalMottattdato);
    }

    private void replaceBarnWith18YearOldWhenNecessary(String youngster, String barnCloseTo18OrMore, boolean useHistoricalMottattdato) throws SyntetisertBidragsmeldingException {

        int barnAgeInMonthsAtMottattdato = 12 * 17 + 6;
        int soktFra = barnAgeInMonthsAtMottattdato - (12 * 18 + 1);

        syntetiseringService = syntetiseringServiceBuilder(useHistoricalMottattdato);

        List<String> availableBarn = new ArrayList<>(Arrays.asList(youngster, barnCloseTo18OrMore));

        List<SyntetisertBidragsmelding> bidragsmeldinger = new ArrayList<>(
                Arrays.asList(
                        SyntetisertBidragsmelding.builder()
                                .barnAlderIMnd(barnAgeInMonthsAtMottattdato)
                                .soknadstype(KodeSoknTypeConstants.SOKNAD)
                                .soktFra(Integer.toString(soktFra))
                                .soknadFra(KodeSoknFraConstants.BM)
                                .soktOm(KodeSoknGrKomConstants.BIDRAG_18_AAR_INNKREVING).build()));

        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(availableBarn);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(availableBarn.size()))
                .thenReturn(bidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(youngster, env))
                .thenReturn(RelasjonsResponse.builder().fnr(youngster).relasjoner(relasjoner).build());

        when(hodejegerenConsumer.getRelasjoner(barnCloseTo18OrMore, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barnCloseTo18OrMore).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> bidragsmeldingerMedPersoner = syntetiseringService.generateBidragsmeldinger(
                new SyntetiserBisysRequest(avspillergruppeId, env, availableBarn.size()));

        SyntetisertBidragsmelding bidragsmelding = bidragsmeldingerMedPersoner.get(0);
        String baFnr = bidragsmelding.getBarn();
        LocalDate mottattdato = LocalDate.parse(
                bidragsmelding.getMottattDato(), DateTimeFormat.forPattern(Soknad.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST));

        int ageInMonthsAtSoktFra = getAgeInMonths(baFnr, mottattdato.minusMonths(Integer.parseInt(bidragsmelding.getSoktFra())).dayOfMonth().withMinimumValue());

        assertThat(baFnr, equalTo(barnCloseTo18OrMore));
        assertThat(ageInMonthsAtSoktFra, equalTo(18 * 12 + 1));
    }

    @Test
    public void adjustSoktFraIfCurrentSoktFraIsBeforeChildsBirthdate() throws SyntetisertBidragsmeldingException {

        List<String> tilgjengeligeBarn = new ArrayList<>(Arrays.asList(barnUnborn));

        List<SyntetisertBidragsmelding> bidragsmeldinger = new ArrayList<>(
                Arrays.asList(
                        SyntetisertBidragsmelding.builder()
                                .barnAlderIMnd(36)
                                .soknadstype(KodeSoknTypeConstants.SOKNAD)
                                .soktFra("2")
                                .soknadFra(KodeSoknFraConstants.BM)
                                .soktOm(KodeSoknGrKomConstants.BIDRAG_INNKREVING).build()));

        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(tilgjengeligeBarn);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(tilgjengeligeBarn.size()))
                .thenReturn(bidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(barnUnborn, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barnUnborn).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> bidragsmeldingerMedPersoner = syntetiseringService.generateBidragsmeldinger(new SyntetiserBisysRequest(avspillergruppeId, env, tilgjengeligeBarn.size()));

        SyntetisertBidragsmelding bidragsmelding = bidragsmeldingerMedPersoner.get(0);

        LocalDate mottattdato = LocalDate.parse(bidragsmelding.getMottattDato(), DateTimeFormat.forPattern(Soknad.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST));
        LocalDate soktFraDato = LocalDate.parse(bidragsmelding.getMottattDato(), DateTimeFormat.forPattern(Soknad.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST))
                .minusMonths(Integer.parseInt(bidragsmelding.getSoktFra()));

        int ageInMonthsAtSoktFra = getAgeInMonths(bidragsmelding.getBarn(), soktFraDato);
        int ageInMonthsAtMottattdato = getAgeInMonths(bidragsmelding.getBarn(), mottattdato);

        assertThat(bidragsmelding.getSoktFra(), equalTo(Integer.toString(ageInMonthsAtMottattdato - ageInMonthsAtSoktFra)));
        assertThat(bidragsmelding.getBarnAlderIMnd(), equalTo(ageInMonthsAtMottattdato));
        assertThat(ageInMonthsAtSoktFra, greaterThanOrEqualTo(0));

    }

    @Test
    public void adjustSoktFraIfCurrentIsOver18YearOldAtSoktFra() throws SyntetisertBidragsmeldingException {

        List<String> tilgjengeligeBarn = new ArrayList<>(Arrays.asList(barn18YO));

        List<SyntetisertBidragsmelding> bidragsmeldinger = new ArrayList<>(
                Arrays.asList(
                        SyntetisertBidragsmelding.builder()
                                .barnAlderIMnd(36)
                                .soknadstype(KodeSoknTypeConstants.SOKNAD)
                                .soktFra("-13")
                                .soknadFra(KodeSoknFraConstants.BM)
                                .soktOm(KodeSoknGrKomConstants.BIDRAG_INNKREVING).build()));

        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(tilgjengeligeBarn);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(tilgjengeligeBarn.size()))
                .thenReturn(bidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(barn18YO, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn18YO).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> bidragsmeldingerMedPersoner = syntetiseringService.generateBidragsmeldinger(new SyntetiserBisysRequest(avspillergruppeId, env, tilgjengeligeBarn.size()));

        SyntetisertBidragsmelding bidragsmelding = bidragsmeldingerMedPersoner.get(0);

        LocalDate soktFraDato = LocalDate.parse(bidragsmelding.getMottattDato(), DateTimeFormat.forPattern(Soknad.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST))
                .minusMonths(Integer.parseInt(bidragsmelding.getSoktFra()));

        int ageInMonthsAtSoktFra = getAgeInMonths(bidragsmelding.getBarn(), soktFraDato);

        assertThat(ageInMonthsAtSoktFra, lessThan(18 * 12));

    }

    @Test
    public void adjustHistoricalMottattdatoIfBeforeLowerBound() throws SyntetisertBidragsmeldingException {

        syntetiseringService = syntetiseringServiceBuilder(true);

        List<String> tilgjengeligeBarn = new ArrayList<>(Arrays.asList(barn16YOAtMinMottattdato));

        List<SyntetisertBidragsmelding> bidragsmeldinger = new ArrayList<>(
                Arrays.asList(
                        SyntetisertBidragsmelding.builder()
                                .barnAlderIMnd(113)
                                .soknadstype(KodeSoknTypeConstants.SOKNAD)
                                .soktFra("6")
                                .soknadFra(KodeSoknFraConstants.BM)
                                .soktOm(KodeSoknGrKomConstants.BIDRAG_INNKREVING).build()));

        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(tilgjengeligeBarn);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(tilgjengeligeBarn.size()))
                .thenReturn(bidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(barn16YOAtMinMottattdato, env))
                .thenReturn(RelasjonsResponse.builder().fnr(barn16YOAtMinMottattdato).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> bidragsmeldingerMedPersoner = syntetiseringService.generateBidragsmeldinger(new SyntetiserBisysRequest(avspillergruppeId, env, tilgjengeligeBarn.size()));

        SyntetisertBidragsmelding bidragsmelding = bidragsmeldingerMedPersoner.get(0);

        LocalDate mottattdato = LocalDate.parse(bidragsmelding.getMottattDato(), DateTimeFormat.forPattern(Soknad.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST));

        assertThat(mottattdato, greaterThanOrEqualTo((SyntetiseringService.MIN_MOTTATT_DATO)));

    }

    @Test
    public void useYoungerBarnIfCurrentIsTooOldAndUseHistoricalMottattdato() throws SyntetisertBidragsmeldingException {
        boolean useHistoricalMottattdato = true;
        useYoungsterIfCurrentIsTooOld(barn21AtMinMottattdato, barn3YO, useHistoricalMottattdato);
    }

    @Test
    public void useYoungerBarnIfCurrentIsTooOld() throws SyntetisertBidragsmeldingException {
        boolean useHistoricalMottattdato = false;
        useYoungsterIfCurrentIsTooOld(barn21YO, barn3YO, useHistoricalMottattdato);
    }

    private void useYoungsterIfCurrentIsTooOld(String tooOldBarn, String youngster, boolean useHistoricalMottattdato) throws SyntetisertBidragsmeldingException {

        int numberOfBarnInValidAgeRange = 1;

        SyntetiseringService syntetiseringService = syntetiseringServiceBuilder(useHistoricalMottattdato);

        List<String> availableBarn = new ArrayList<>(Arrays.asList(tooOldBarn, youngster));

        List<SyntetisertBidragsmelding> bidragsmeldinger = new ArrayList<>(
                Arrays.asList(
                        SyntetisertBidragsmelding.builder()
                                .barnAlderIMnd(453)
                                .soknadstype(KodeSoknTypeConstants.OPPHOR)
                                .soktFra("257")
                                .soknadFra(KodeSoknFraConstants.NAV)
                                .soktOm(KodeSoknGrKomConstants.FORSKUDD).build()));

        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(availableBarn);
        when(bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(numberOfBarnInValidAgeRange))
                .thenReturn(bidragsmeldinger);
        when(hodejegerenConsumer.getRelasjoner(youngster, env))
                .thenReturn(RelasjonsResponse.builder().fnr(youngster).relasjoner(relasjoner).build());

        List<SyntetisertBidragsmelding> bidragsmeldingerMedPersoner = syntetiseringService.generateBidragsmeldinger(new SyntetiserBisysRequest(avspillergruppeId, env, availableBarn.size()));

        SyntetisertBidragsmelding bidragsmelding = bidragsmeldingerMedPersoner.get(0);
        assertThat(bidragsmelding.getBarn(), equalTo(youngster));
    }

    @Test(expected = SyntetisertBidragsmeldingException.class)
    public void skipIdentIfTooOldAndUseHistoricalMottattdato() throws SyntetisertBidragsmeldingException {

        boolean useHistoricalMottattdato = true;
        skipIdentIfTooOld(barn21AtMinMottattdato, useHistoricalMottattdato);
    }

    @Test(expected = SyntetisertBidragsmeldingException.class)
    public void skipIdentIfTooOld() throws SyntetisertBidragsmeldingException {
        boolean useHistoricalMottattdato = false;
        skipIdentIfTooOld(barn21AtMinMottattdato, useHistoricalMottattdato);
    }

    private void skipIdentIfTooOld(String fnrOldBarn, boolean useHistoricalMottattdato) throws SyntetisertBidragsmeldingException {
        SyntetiseringService syntetiseringService = syntetiseringServiceBuilder(useHistoricalMottattdato);

        List<String> availableBarn = new ArrayList<>(Arrays.asList(fnrOldBarn));

        when(hodejegerenConsumer.getFoedte(avspillergruppeId)).thenReturn(availableBarn);

        syntetiseringService.generateBidragsmeldinger(new SyntetiserBisysRequest(avspillergruppeId, env, availableBarn.size()));
    }

    private static LocalDate getBirthdate(String baFnr) {
        String birthdateStr = baFnr.substring(0, 6);
        LocalDate birthdate = LocalDate.parse(birthdateStr, DateTimeFormat.forPattern("ddMMyy"));
        return birthdate;
    }

    private static int getAgeInMonths(String baFnr, LocalDate dateMeasured) {
        LocalDate fodselsdato = getBirthdate(baFnr);

        return Months.monthsBetween(fodselsdato.dayOfMonth().withMinimumValue(), dateMeasured.dayOfMonth().withMinimumValue()).getMonths();
    }

}
