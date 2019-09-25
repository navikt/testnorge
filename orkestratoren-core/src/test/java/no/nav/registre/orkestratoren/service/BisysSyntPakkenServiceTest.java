package no.nav.registre.orkestratoren.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import no.nav.registre.orkestratoren.consumer.rs.BisysSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;

@RunWith(MockitoJUnitRunner.class)
public class BisysSyntPakkenServiceTest {

    @Mock
    private BisysSyntConsumer bisysSyntConsumer;

    @InjectMocks
    private BisysSyntPakkenService bisysSyntPakkenService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;

    @Test
    public void shouldGenerereBistandsmeldinger() {
        SyntetiserBisysRequest syntetiserBisysRequest = new SyntetiserBisysRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(bisysSyntConsumer.startSyntetisering(syntetiserBisysRequest)).thenReturn(ResponseEntity.ok().build());

        bisysSyntPakkenService.genererBistandsmeldinger(syntetiserBisysRequest);

        verify(bisysSyntConsumer).startSyntetisering(syntetiserBisysRequest);
    }
}