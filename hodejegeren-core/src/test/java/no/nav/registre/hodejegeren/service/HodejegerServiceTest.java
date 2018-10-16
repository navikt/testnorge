package no.nav.registre.hodejegeren.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

@RunWith(MockitoJUnitRunner.class)
public class HodejegerServiceTest {
    
    @Mock
    private TpsSyntetisererenConsumer tpsSyntetisererenConsumer;
    
    @InjectMocks
    private HodejegerService hodejegerService;
    
    @Test
    public void puttIdenterIMeldingerOgLagre() {
        final HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put("01", 3);
        antallMeldingerPerAarsakskode.put("02", 4);
    
        List<RsMeldingstype> treSkdmeldinger = Arrays.asList(new RsMeldingstype1Felter(), new RsMeldingstype1Felter(),new RsMeldingstype1Felter());
        List<RsMeldingstype> fireSkdmeldinger = Arrays.asList(new RsMeldingstype1Felter(), new RsMeldingstype1Felter(),new RsMeldingstype1Felter(), new RsMeldingstype1Felter());
    
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(any(), eq(3))).thenReturn(treSkdmeldinger);
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(any(), eq(4))).thenReturn(fireSkdmeldinger);
        
        final List<Long> ids = hodejegerService.puttIdenterIMeldingerOgLagre(new GenereringsOrdreRequest(123L, "t1", antallMeldingerPerAarsakskode));
    
        Mockito.verify(tpsSyntetisererenConsumer).getSyntetiserteSkdmeldinger("01", 3);
        Mockito.verify(tpsSyntetisererenConsumer).getSyntetiserteSkdmeldinger("02", 4);
    }
}