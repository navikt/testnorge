package no.nav.registre.skd.service;

import static no.nav.registre.skd.domain.Endringskoder.ENDRING_OPPHOLDSTILLATELSE;
import static no.nav.registre.skd.testutils.Utils.testLoggingInClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import no.nav.registre.skd.consumer.SyntTpsConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.domain.Endringskoder;
import no.nav.registre.skd.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    @Mock
    private SyntTpsConsumer syntTpsConsumer;

    @Mock
    private NyeIdenterService nyeIdenterService;

    @Mock
    private ValidationService validationService;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @Mock
    private FoedselService foedselService;

    @Mock
    private EksisterendeIdenterService eksisterendeIdenterService;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    /**
     * Test-scenario: NÅR metoden puttIdenterIMeldingerOgLagre blir kalt med en map med endringskoder og tilhørende antall
     * meldinger, SÅ skal - TPS Syntetisereren konsumeres for å hente det rette antallet meldinger per endringskode - Alle meldinger
     * blir validert på skd-formatet: maksstørrelsen for hvert felt er oppfylt - alle meldingene lagres på korrekt gruppeId i TPSF -
     * kaller TpsfConsumer
     */
    @Test
    public void shouldPuttIdenterIMeldingerOgLagre() {
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(Endringskoder.FOEDSELSMELDING.getEndringskode(), 3);
        antallMeldingerPerEndringskode.put(Endringskoder.INNVANDRING.getEndringskode(), 4);
        final var GRUPPE_ID = 123L;

        List<RsMeldingstype> treSkdmeldinger = Arrays.asList(new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter());
        List<RsMeldingstype> fireSkdmeldinger = Arrays.asList(new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter());

        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(any(), eq(3))).thenReturn(treSkdmeldinger);
        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(any(), eq(4))).thenReturn(fireSkdmeldinger);

        syntetiseringService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(GRUPPE_ID, "t1", antallMeldingerPerEndringskode));

        verify(syntTpsConsumer).getSyntetiserteSkdmeldinger(Endringskoder.FOEDSELSMELDING.getEndringskode(), 3);
        verify(syntTpsConsumer).getSyntetiserteSkdmeldinger(Endringskoder.INNVANDRING.getEndringskode(), 4);

        verify(validationService).logAndRemoveInvalidMessages(eq(treSkdmeldinger), any());
        verify(validationService).logAndRemoveInvalidMessages(eq(fireSkdmeldinger), any());

        ArgumentCaptor<List<RsMeldingstype>> captor = ArgumentCaptor.forClass(List.class);
        verify(tpsfConsumer, times(2)).saveSkdEndringsmeldingerInTPSF(eq(GRUPPE_ID), captor.capture());
        final List<List<RsMeldingstype>> actualSavedSkdmeldinger = captor.getAllValues();
        assertEquals(fireSkdmeldinger, actualSavedSkdmeldinger.get(0));
        assertEquals(treSkdmeldinger, actualSavedSkdmeldinger.get(1));
    }

    @Test
    public void sjekkAtNyeIdenterBlirKaltForRiktigeEndringskoder() {
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(Endringskoder.FOEDSELSMELDING.getEndringskode(), 1);
        antallMeldingerPerEndringskode.put(Endringskoder.INNVANDRING.getEndringskode(), 1);
        antallMeldingerPerEndringskode.put(Endringskoder.FOEDSELSNUMMERKORREKSJON.getEndringskode(), 1);
        antallMeldingerPerEndringskode.put(Endringskoder.TILDELING_DNUMMER.getEndringskode(), 1);

        antallMeldingerPerEndringskode.put("0310", 1); // stikkprøve - endringskoder som ikke skal ha nye identer
        antallMeldingerPerEndringskode.put("0410", 1); // stikkprøve - endringskoder som ikke skal ha nye identer

        List<RsMeldingstype> meldinger01 = Collections.singletonList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger02 = Collections.singletonList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger39 = Collections.singletonList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger91 = Collections.singletonList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger03 = Collections.singletonList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger04 = Collections.singletonList(new RsMeldingstype1Felter());

        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(Endringskoder.FOEDSELSMELDING.getEndringskode(), 1)).thenReturn(meldinger01);
        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(Endringskoder.INNVANDRING.getEndringskode(), 1)).thenReturn(meldinger02);
        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(Endringskoder.FOEDSELSNUMMERKORREKSJON.getEndringskode(), 1)).thenReturn(meldinger39);
        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(Endringskoder.TILDELING_DNUMMER.getEndringskode(), 1)).thenReturn(meldinger91);

        final var fodselsdato = "111111";
        final var personnummer = "22222";

        Answer<Object> setFnrInFirstSkdmeldingAndReturnFnr = invocation -> {
            List<RsMeldingstype> melding = invocation.getArgument(1);
            if (null == melding) {
                return new ArrayList<>();
            }
            ((RsMeldingstype1Felter) melding.get(0)).setFodselsdato(fodselsdato);
            ((RsMeldingstype1Felter) melding.get(0)).setPersonnummer(personnummer);
            return Collections.singletonList(fodselsdato + personnummer);
        };

        when(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(any(), any())).thenAnswer(setFnrInFirstSkdmeldingAndReturnFnr);
        when(foedselService.behandleFoedselsmeldinger(any(), any(), any())).thenAnswer(setFnrInFirstSkdmeldingAndReturnFnr);

        syntetiseringService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerEndringskode));

        assertEquals(fodselsdato, ((RsMeldingstype1Felter) meldinger01.get(0)).getFodselsdato());
        assertEquals(fodselsdato, ((RsMeldingstype1Felter) meldinger02.get(0)).getFodselsdato());
        assertEquals(fodselsdato, ((RsMeldingstype1Felter) meldinger39.get(0)).getFodselsdato());
        assertEquals(fodselsdato, ((RsMeldingstype1Felter) meldinger91.get(0)).getFodselsdato());

        assertNull(((RsMeldingstype1Felter) meldinger03.get(0)).getFodselsdato());
        assertNull(((RsMeldingstype1Felter) meldinger03.get(0)).getPersonnummer());
        assertNull(((RsMeldingstype1Felter) meldinger04.get(0)).getFodselsdato());
        assertNull(((RsMeldingstype1Felter) meldinger04.get(0)).getPersonnummer());
    }

    /**
     * Testscenario: Testnorge-Skd må filtrere bort endringskoder som den for øyeblikket ikke støtter.
     */
    @Test
    public void shouldFiltrereEndringskodeneSomBestillesFraTpsSyntetisereren() {
        var requestedEndringskoder = Arrays.asList("tull", "0010", "0x", "100", "9110", "5110", "5610", "8110", "9810",
                "4310", "3210", "0211", "0110", "3910", "0610", "0710", "1010", "1110", "1410", "1810");
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        for (var requestedEndringskode : requestedEndringskoder) {
            antallMeldingerPerEndringskode.put(requestedEndringskode, 0);
        }

        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(any(), any())).thenReturn(new ArrayList<>());

        syntetiseringService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerEndringskode));

        final var inOrder = Mockito.inOrder(syntTpsConsumer);
        for (var endringskode : Arrays.asList("9110", "0211", "0110", "3910",
                "0610", "0710", "1010", "1110", "1410", "1810", "5110", "5610", "8110", "9810", "4310", "3210")) {
            inOrder.verify(syntTpsConsumer).getSyntetiserteSkdmeldinger(eq(endringskode), any());
        }
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Testscenario: HVIS kall til Tpsf gjennom {@link TpsfConsumer#saveSkdEndringsmeldingerInTPSF} feiler, og det kastes en
     * exception, skal denne catches og nye, rekvirerte identer som ikke blir lagret i TPSF logges.
     */
    @Test
    public void shouldCatchExceptionAndLogIfTpsfFails() {
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(Endringskoder.FOEDSELSMELDING.getEndringskode(), 2);
        antallMeldingerPerEndringskode.put(Endringskoder.INNVANDRING.getEndringskode(), 2);

        Logger logger = (Logger) LoggerFactory.getLogger(SyntetiseringService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        RsMeldingstype1Felter melding = new RsMeldingstype1Felter();
        melding.setFodselsdato("010101");
        melding.setPersonnummer("01010");

        when(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(any(), any())).thenThrow(RuntimeException.class);
        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(any(), anyInt())).thenReturn(Collections.singletonList(melding));

        syntetiseringService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerEndringskode));
        verify(tpsfConsumer, times(2)).saveSkdEndringsmeldingerInTPSF(any(), eq(Collections.singletonList(melding)));

        assertTrue(listAppender.list.toString().contains("Noe feilet under lagring til TPSF"));
        assertTrue(listAppender.list.toString().contains("01010101010"));
    }

    /**
     * Testscenario: HVIS RuntimeException blir kastet som det ikke finnes spesifikk behandling for, SÅ skal database-id-ene hos
     * TPSF for de allerede lagrede skdmeldingene loggføres og feilmeldingen kastes videre. Idene i loggingen skal skrives med range
     * for å spare plass
     */
    @Test
    public void generellFeilhaandteringBurdeLoggfoereLagredeSkdmeldingenesIdMedRange() {
        ListAppender<ILoggingEvent> listAppender = testLoggingInClass(SyntetiseringService.class);
        var ids = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 8L, 10L, 11L, 13L, 14L, 15L, 17L, 18L);
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(Endringskoder.INNFLYTTING_ANNEN_KOMMUNE.getEndringskode(), ids.size());
        antallMeldingerPerEndringskode.put(ENDRING_OPPHOLDSTILLATELSE.getEndringskode(), 1);

        when(syntTpsConsumer.getSyntetiserteSkdmeldinger(any(), any())).thenReturn(new ArrayList<>());
        var testfeilmelding = "testfeilmelding";
        doThrow(new RuntimeException(testfeilmelding))
                .when(eksisterendeIdenterService).behandleEksisterendeIdenter(any(), any(), eq(ENDRING_OPPHOLDSTILLATELSE), any());
        when(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(any(), any())).thenReturn(ids);

        var response = syntetiseringService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerEndringskode));

        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.CONFLICT)));
        assertThat(listAppender.list.size(), is(equalTo(6)));
        assertThat(listAppender.list.toString(), containsString(String.format("Skdmeldinger som er lagret i TPSF, men som ikke ble sendt til TPS har følgende id-er i TPSF: %s",
                "[1 - 6, 8, 10 - 11, 13 - 15, 17 - 18]")));
    }
}