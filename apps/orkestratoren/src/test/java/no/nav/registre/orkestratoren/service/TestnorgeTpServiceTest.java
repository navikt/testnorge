package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeTpConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@ExtendWith(MockitoExtension.class)
public class TestnorgeTpServiceTest {

    @Mock
    private TestnorgeTpConsumer testnorgeTpConsumer;

    @InjectMocks
    private TestnorgeTpService testnorgeTpService;

    @Test
    public void shouldGenerereTp() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;
        ResponseEntity<String> expectedResponse = ResponseEntity.ok().build();
        var syntetiserTpRequest = new SyntetiserTpRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeTpConsumer.startSyntetisering(syntetiserTpRequest)).thenReturn(expectedResponse);

        var response = testnorgeTpService.genererTp(syntetiserTpRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        verify(testnorgeTpConsumer).startSyntetisering(syntetiserTpRequest);
    }
}