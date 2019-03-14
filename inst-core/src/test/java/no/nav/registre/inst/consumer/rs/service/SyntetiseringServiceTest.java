package no.nav.registre.inst.consumer.rs.service;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import no.nav.registre.inst.consumer.rs.HodejegerenConsumer;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.institusjonsforhold.Institusjonsforholdsmelding;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    @Mock
    private InstSyntetisererenConsumer instSyntetisererenConsumer;

    @Mock
    private Inst2Consumer inst2Consumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private Random rand;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallMeldinger = 1;

    @Test
    public void shouldOppretteInstitusjonsmeldinger() {
        SyntetiserInstRequest syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallMeldinger);
        List<String> utvalgteIdenter = new ArrayList<>(Arrays.asList("01010101010"));

        List<Institusjonsforholdsmelding> syntetiserteMeldinger = new ArrayList<>();
        syntetiserteMeldinger.add(Institusjonsforholdsmelding.builder().build());

        when(instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger)).thenReturn(syntetiserteMeldinger);
        when(hodejegerenConsumer.finnLevendeIdenter(avspillergruppeId)).thenReturn(utvalgteIdenter);

        syntetiseringService.finnSyntetiserteMeldinger(syntetiserInstRequest);

        verify(instSyntetisererenConsumer).hentInstMeldingerFromSyntRest(antallMeldinger);
        verify(hodejegerenConsumer).finnLevendeIdenter(avspillergruppeId);
        verify(inst2Consumer, times(2)).hentTokenTilInst2();
        verify(inst2Consumer).hentInstitusjonsoppholdFraInst2(anyMap(), anyString());
    }
}
