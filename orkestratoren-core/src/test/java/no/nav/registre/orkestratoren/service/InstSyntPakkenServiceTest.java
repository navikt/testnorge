package no.nav.registre.orkestratoren.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.consumer.rs.InstSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@RunWith(MockitoJUnitRunner.class)
public class InstSyntPakkenServiceTest {

    @Mock
    private InstSyntConsumer instSyntConsumer;

    @InjectMocks
    private InstSyntPakkenService instSyntPakkenService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String expectedResponse = "someResponse";

    @Test
    public void shouldGenerereInstitusjonsforhold() {
        SyntetiserInstRequest syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(instSyntConsumer.startSyntetisering(syntetiserInstRequest)).thenReturn(expectedResponse);

        instSyntPakkenService.genererInstitusjonsforhold(syntetiserInstRequest);

        verify(instSyntConsumer).startSyntetisering(syntetiserInstRequest);
    }
}