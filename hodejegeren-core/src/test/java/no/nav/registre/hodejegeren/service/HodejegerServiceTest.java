package no.nav.registre.hodejegeren.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HodejegerServiceTest {

    @Mock
    private TpsSyntetisererenConsumer tpsSyntetisererenConsumer;

    @Mock
    private NyeIdenterService nyeIdenterService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @Mock
    private ValidationService validationService;

    @Mock
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Mock
    private FoedselService foedselService;

    @InjectMocks
    private HodejegerService hodejegerService;

    /**
     * Test-scenario: NÅR metoden puttIdenterIMeldingerOgLagre blir kalt med en map med årsakskoder og tilhørende antall meldinger,
     * SÅ skal
     * - TPS Syntetisereren konsumeres for å hente det rette antallet meldinger per årsakskode
     * - Alle meldinger blir validert på skd-formatet: maksstørrelsen for hvert felt er oppfylt
     * - alle meldingene lagres på korrekt gruppeId i TPSF - kaller TpsfConsumer
     */
    @Test
    public void shouldPuttIdenterIMeldingerOgLagre() {
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 3);
        antallMeldingerPerEndringskode.put("0211", 4);
        final long GRUPPE_ID = 123L;

        List<RsMeldingstype> treSkdmeldinger = Arrays.asList(new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter());
        List<RsMeldingstype> fireSkdmeldinger = Arrays.asList(new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter());

        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(any(), eq(3))).thenReturn(treSkdmeldinger);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(any(), eq(4))).thenReturn(fireSkdmeldinger);

        final List<Long> ids = hodejegerService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(GRUPPE_ID, "t1", antallMeldingerPerEndringskode));

        verify(tpsSyntetisererenConsumer).getSyntetiserteSkdmeldinger("0110", 3);
        verify(tpsSyntetisererenConsumer).getSyntetiserteSkdmeldinger("0211", 4);

        verify(validationService).logAndRemoveInvalidMessages(treSkdmeldinger);
        verify(validationService).logAndRemoveInvalidMessages(fireSkdmeldinger);

        ArgumentCaptor<List<RsMeldingstype>> captor = ArgumentCaptor.forClass(List.class);
        verify(tpsfConsumer, times(2)).saveSkdEndringsmeldingerInTPSF(eq(GRUPPE_ID), captor.capture());
        final List<List<RsMeldingstype>> actualSavedSkdmeldinger = captor.getAllValues();
        assertEquals(fireSkdmeldinger, actualSavedSkdmeldinger.get(0));
        assertEquals(treSkdmeldinger, actualSavedSkdmeldinger.get(1));
    }

    @Test
    public void sjekkAtNyeIdenterBlirKaltForRiktigeEndringskoder() {
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 1);
        antallMeldingerPerEndringskode.put("0211", 1);
        antallMeldingerPerEndringskode.put("3910", 1);
        antallMeldingerPerEndringskode.put("9110", 1);

        antallMeldingerPerEndringskode.put("0310", 1); //stikkprøve - endringskoder som ikke skal ha nye identer
        antallMeldingerPerEndringskode.put("0410", 1); //stikkprøve - endringskoder som ikke skal ha nye identer

        List<RsMeldingstype> meldinger01 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger02 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger39 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger91 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger03 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger04 = Arrays.asList(new RsMeldingstype1Felter());

        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("0110", 1)).thenReturn(meldinger01);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("0211", 1)).thenReturn(meldinger02);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("3910", 1)).thenReturn(meldinger39);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("9110", 1)).thenReturn(meldinger91);

        final String fodselsdato = "111111";
        final String personnummer = "22222";

        when(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(any(), any())).thenAnswer(invocation -> {
            List<RsMeldingstype> melding = invocation.getArgument(1);
            if (null == melding) {
                return new ArrayList<>();
            }
            ((RsMeldingstype1Felter) melding.get(0)).setFodselsdato(fodselsdato);
            ((RsMeldingstype1Felter) melding.get(0)).setPersonnummer(personnummer);
            return Arrays.asList(fodselsdato + personnummer);
        });

        when(foedselService.behandleFoedselsmeldinger(any(), any(), any())).thenAnswer(invocation -> {
            List<RsMeldingstype> melding = invocation.getArgument(1);
            if (null == melding) {
                return new ArrayList<>();
            }
            ((RsMeldingstype1Felter) melding.get(0)).setFodselsdato(fodselsdato);
            ((RsMeldingstype1Felter) melding.get(0)).setPersonnummer(personnummer);
            return Arrays.asList(fodselsdato + personnummer);
        });

        hodejegerService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerEndringskode));

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
     * Testscenario: Hodejegeren må filtrere bort endringskoder som hodejegeren for øyeblikket ikke har støtte for.
     */
    @Test
    public void shouldFiltrereEndringskodeneSomBestillesFraTpsSyntetisereren() {
        List<String> requestedEndringskoder = Arrays.asList("tull", "0010", "0x", "100", "9110", "5110", "5610", "8110", "9810",
                "4310", "3210", "0211", "0110", "3910", "0610", "0710", "1010", "1110", "1410", "1810");
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        for (String requestedEndringskode : requestedEndringskoder) {
            antallMeldingerPerEndringskode.put(requestedEndringskode, 0);
        }

        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(any(), any())).thenReturn(new ArrayList<>());

        hodejegerService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerEndringskode));

        final InOrder inOrder = Mockito.inOrder(tpsSyntetisererenConsumer);
        for (String endringskode : Arrays.asList("9110", "0211", "0110", "3910", "0610", "0710", "1010", "1110", "1410", "1810", "5110", "5610", "8110", "9810", "4310", "3210")) {
            inOrder.verify(tpsSyntetisererenConsumer).getSyntetiserteSkdmeldinger(eq(endringskode), any());
        }
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Testscenario: HVIS kall til Tpsf gjennom {@link TpsfConsumer#saveSkdEndringsmeldingerInTPSF} feiler, og det
     * kastes en exception, skal denne catches og feilen logges.
     */
    @Test
    public void shouldCatchExceptionAndLogIfTpsfFails() {
        final HashMap<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 2);
        antallMeldingerPerEndringskode.put("0211", 2);

        Logger logger = (Logger) LoggerFactory.getLogger(HodejegerService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        when(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(any(), any())).thenThrow(RuntimeException.class);

        hodejegerService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerEndringskode));

        verify(tpsfConsumer, times(2)).saveSkdEndringsmeldingerInTPSF(any(), any());

        assertEquals(4, listAppender.list.size());
        assertTrue(listAppender.list.get(0).toString().contains("Noe feilet under lagring til TPSF"));
        assertTrue(listAppender.list.get(2).toString().contains("Noe feilet under lagring til TPSF"));
    }
}