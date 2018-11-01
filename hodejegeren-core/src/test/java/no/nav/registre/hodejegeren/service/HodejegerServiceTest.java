package no.nav.registre.hodejegeren.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

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
    public void puttIdenterIMeldingerOgLagre() {
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
    public void sjekkAtNyeIdenterBlirKaltForRiktigeAarsakskoder() {
        final HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put("0110", 1);
        antallMeldingerPerAarsakskode.put("0211", 1);
        antallMeldingerPerAarsakskode.put("3910", 1);
        antallMeldingerPerAarsakskode.put("9110", 1);
        
        antallMeldingerPerAarsakskode.put("0310", 1); //stikkprøve - aarsakskoder som ikke skal ha nye identer
        antallMeldingerPerAarsakskode.put("0410", 1); //stikkprøve - aarsakskoder som ikke skal ha nye identer
        
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
        
        hodejegerService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerAarsakskode));
        
        assertEquals(fodselsdato, ((RsMeldingstype1Felter) meldinger01.get(0)).getFodselsdato());
        assertEquals(fodselsdato, ((RsMeldingstype1Felter) meldinger02.get(0)).getFodselsdato());
        assertEquals(fodselsdato, ((RsMeldingstype1Felter) meldinger39.get(0)).getFodselsdato());
        assertEquals(fodselsdato, ((RsMeldingstype1Felter) meldinger91.get(0)).getFodselsdato());
        
        assertNull(((RsMeldingstype1Felter) meldinger03.get(0)).getFodselsdato());
        assertNull(((RsMeldingstype1Felter) meldinger03.get(0)).getPersonnummer());
        assertNull(((RsMeldingstype1Felter) meldinger04.get(0)).getFodselsdato());
        assertNull(((RsMeldingstype1Felter) meldinger04.get(0)).getPersonnummer());
    }
    
    @Test
    public void shouldFiltrereOgSortereAarsakskodeneSomBestillesFraTpsSyntetisereren() {
        List<String> requestedAarsakskoder = Arrays.asList("tull", "0010", "0x", "100", "9110", "5110", "5610", "8110", "9810",
                "8510", "4310", "3210", "0211", "0110", "3910", "0610", "0710", "1010", "1110", "1410", "1810");
        final HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        for (String requestedAarsakskode : requestedAarsakskoder) {
            antallMeldingerPerAarsakskode.put(requestedAarsakskode, 0);
        }
        
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(any(), any())).thenReturn(new ArrayList<>());
        
        hodejegerService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerAarsakskode));
        
        final InOrder inOrder = Mockito.inOrder(tpsSyntetisererenConsumer);
        for (String aarsakskode : Arrays.asList("9110", "0211", "0110", "3910", "0610", "0710", "1010", "1110", "1410", "1810", "5110", "5610", "8110", "9810", "8510", "4310", "3210")) {
            inOrder.verify(tpsSyntetisererenConsumer).getSyntetiserteSkdmeldinger(eq(aarsakskode), any());
        }
        inOrder.verifyNoMoreInteractions();
    }
}