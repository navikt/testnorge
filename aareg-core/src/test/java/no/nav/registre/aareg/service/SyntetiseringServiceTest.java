package no.nav.registre.aareg.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenConsumer;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    @Mock
    private Random rand;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Mock
    private AaregstubConsumer aaregstubConsumer;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallMeldinger = 1;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";

    @Test
    public void shouldOppretteArbeidshistorikk() {
        SyntetiserAaregRequest syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallMeldinger);
        List<String> fnrs = new ArrayList<>();
        fnrs.add(fnr1);
        fnrs.add(fnr2);

        Map<String, List<Map<String, String>>> syntetiserteMeldinger = new HashMap<>();

        when(hodejegerenConsumer.finnLevendeIdenter(avspillergruppeId)).thenReturn(fnrs);
        when(aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(anyList())).thenReturn(syntetiserteMeldinger);
        when(aaregstubConsumer.hentEksisterendeIdenter()).thenReturn(new ArrayList<>());

        syntetiseringService.opprettArbeidshistorikk(syntetiserAaregRequest);

        verify(aaregstubConsumer).sendTilAaregstub(syntetiserteMeldinger);
    }
}
