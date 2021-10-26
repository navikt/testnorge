package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@ExtendWith(MockitoExtension.class)

public class TestnorgeArenaServiceTest {

    @Mock
    private TestnorgeArenaConsumer testnorgeArenaConsumer;

    @InjectMocks
    private TestnorgeArenaService testnorgeArenaService;

    private final Long avspillergruppeId = 123L;
    private final String miljoe = "t1";
    private final int antallNyeIdenter = 2;
    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private final List<String> expectedIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr2));

    @Test
    public void shouldOppretteArbeidssokereIArena() {
        var syntetiserArenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeArenaConsumer.opprettArbeidsoekere(syntetiserArenaRequest, false)).thenReturn(expectedIdenter);

        var response = testnorgeArenaService.opprettArbeidssokereIArena(syntetiserArenaRequest, false);

        assertThat(response, IsIterableContainingInOrder.contains(fnr1, fnr2));
        verify(testnorgeArenaConsumer).opprettArbeidsoekere(syntetiserArenaRequest, false);
    }

    @Test
    public void shouldOppretteArbeidssokereMedOppfoelgingIArena() {
        var syntetiserArenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeArenaConsumer.opprettArbeidsoekere(syntetiserArenaRequest, true)).thenReturn(expectedIdenter);

        var response = testnorgeArenaService.opprettArbeidssokereIArena(syntetiserArenaRequest, true);

        assertThat(response, IsIterableContainingInOrder.contains(fnr1, fnr2));
        verify(testnorgeArenaConsumer).opprettArbeidsoekere(syntetiserArenaRequest, true);
    }
}