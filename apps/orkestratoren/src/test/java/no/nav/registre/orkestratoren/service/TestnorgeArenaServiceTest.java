package no.nav.registre.orkestratoren.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.registre.orkestratoren.consumer.rs.SyntVedtakshistorikkServiceConsumer;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;


import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@ExtendWith(MockitoExtension.class)
class TestnorgeArenaServiceTest {

    @Mock
    private SyntVedtakshistorikkServiceConsumer syntVedtakshistorikkServiceConsumer;

    @InjectMocks
    private TestnorgeArenaService testnorgeArenaService;

    private final String miljoe = "t1";
    private final int antallNyeIdenter = 2;
    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";

    @Test
    void shouldOppretteArbeidssokereMedOppfoelgingIArena() {
        var syntetiserArenaRequest = new SyntetiserArenaRequest(miljoe, antallNyeIdenter);
        Map<String, NyeBrukereResponse> expectedResponse = new HashMap<>();
        expectedResponse.put(fnr1, NyeBrukereResponse.builder().build() );
        expectedResponse.put(fnr2, NyeBrukereResponse.builder().build() );

        when(syntVedtakshistorikkServiceConsumer.opprettArbeidsoekereMedOppfoelging(syntetiserArenaRequest)).thenReturn(expectedResponse);

        var response = testnorgeArenaService.opprettArbeidssoekereMedOppfoelgingIArena(syntetiserArenaRequest);

        assertThat(response).hasSize(2).containsKey(fnr1).containsKey(fnr2);
        verify(syntVedtakshistorikkServiceConsumer).opprettArbeidsoekereMedOppfoelging(syntetiserArenaRequest);
    }
}