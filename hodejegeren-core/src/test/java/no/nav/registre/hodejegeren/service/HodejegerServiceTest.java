package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
    
    @InjectMocks
    private HodejegerService hodejegerService;
    
    /**
     * Test-scenario: NÅR metoden puttIdenterIMeldingerOgLagre blir kalt med en map med årsakskoder og tilhørende antall meldinger,
     * SÅ skal
     * - TPS Syntetisereren konsumeres for å hente det rette antallet meldinger per årsakskode
     * - nyeIdenterService kalles
     * - alle meldingene lagres på korrekt gruppeId i TPSF - kaller TpsfConsumer
     */
    @Test
    public void puttIdenterIMeldingerOgLagre() {
        final HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put("01", 3);
        antallMeldingerPerAarsakskode.put("02", 4);
        final long GRUPPE_ID = 123L;
        
        List<RsMeldingstype> treSkdmeldinger = Arrays.asList(new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter());
        List<RsMeldingstype> fireSkdmeldinger = Arrays.asList(new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter(), new RsMeldingstype1Felter());
        
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(any(), eq(3))).thenReturn(treSkdmeldinger);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(any(), eq(4))).thenReturn(fireSkdmeldinger);
        
        final List<Long> ids = hodejegerService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(GRUPPE_ID, "t1", antallMeldingerPerAarsakskode));
        
        Mockito.verify(tpsSyntetisererenConsumer).getSyntetiserteSkdmeldinger("01", 3);
        Mockito.verify(tpsSyntetisererenConsumer).getSyntetiserteSkdmeldinger("02", 4);
        
        verify(nyeIdenterService, times(3)).settInnNyeIdenterITrans1Meldinger(eq(FNR), any());
        verify(nyeIdenterService).settInnNyeIdenterITrans1Meldinger(DNR, null);
        
        ArgumentCaptor<List<RsMeldingstype>> captor = ArgumentCaptor.forClass(List.class);
        verify(tpsfConsumer).saveSkdEndringsmeldingerInTPSF(eq(GRUPPE_ID), captor.capture());
        final List<RsMeldingstype> actualSavedSkdmeldinger = captor.getValue();
        assertEquals(treSkdmeldinger, actualSavedSkdmeldinger.subList(0, 3));
        assertEquals(fireSkdmeldinger, actualSavedSkdmeldinger.subList(3, 7));
    }
    
    @Test
    public void sjekkAtNyeIdenterBlirKaltForRiktigeAarsakskoder() {
        final HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put("01", 1);
        antallMeldingerPerAarsakskode.put("02", 1);
        antallMeldingerPerAarsakskode.put("39", 1);
        antallMeldingerPerAarsakskode.put("91", 1);
        
        antallMeldingerPerAarsakskode.put("03", 1); //stikkprøve - aarsakskoder som ikke skal ha nye identer
        antallMeldingerPerAarsakskode.put("04", 1); //stikkprøve - aarsakskoder som ikke skal ha nye identer
        
        List<RsMeldingstype> meldinger01 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger02 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger39 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger91 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger03 = Arrays.asList(new RsMeldingstype1Felter());
        List<RsMeldingstype> meldinger04 = Arrays.asList(new RsMeldingstype1Felter());
        
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("01", 1)).thenReturn(meldinger01);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("02", 1)).thenReturn(meldinger02);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("39", 1)).thenReturn(meldinger39);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("91", 1)).thenReturn(meldinger91);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("03", 1)).thenReturn(meldinger03);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger("04", 1)).thenReturn(meldinger04);
        
        final String fodselsdato = "111111";
        final String personnummer = "22222";
        
        when(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(any(), any())).thenAnswer(invocation -> {
            List<RsMeldingstype> melding = invocation.getArgument(1);
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
}