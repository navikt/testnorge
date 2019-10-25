package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeTpConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeTpServiceTest {

    @Mock
    private TestnorgeTpConsumer testnorgeTpConsumer;

    @InjectMocks
    private TestnorgeTpService testnorgeTpService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private ResponseEntity expectedResponse = ResponseEntity.ok().build();

    @Test
    public void shouldGenerereTp() {
        SyntetiserTpRequest syntetiserTpRequest = new SyntetiserTpRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeTpConsumer.startSyntetisering(syntetiserTpRequest)).thenReturn(expectedResponse);

        ResponseEntity response = testnorgeTpService.genererTp(syntetiserTpRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        verify(testnorgeTpConsumer).startSyntetisering(syntetiserTpRequest);
    }
}