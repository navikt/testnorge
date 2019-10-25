package no.nav.registre.orkestratoren.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeBisysConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeBisysServiceTest {

    @Mock
    private TestnorgeBisysConsumer testnorgeBisysConsumer;

    @InjectMocks
    private TestnorgeBisysService testnorgeBisysService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;

    @Test
    public void shouldGenerereBistandsmeldinger() {
        SyntetiserBisysRequest syntetiserBisysRequest = new SyntetiserBisysRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeBisysConsumer.startSyntetisering(syntetiserBisysRequest)).thenReturn(ResponseEntity.ok().build());

        testnorgeBisysService.genererBistandsmeldinger(syntetiserBisysRequest);

        verify(testnorgeBisysConsumer).startSyntetisering(syntetiserBisysRequest);
    }
}