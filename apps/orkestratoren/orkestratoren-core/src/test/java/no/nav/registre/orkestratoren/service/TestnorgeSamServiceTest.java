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

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSamConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeSamServiceTest {

    @Mock
    private TestnorgeSamConsumer testnorgeSamConsumer;

    @InjectMocks
    private TestnorgeSamService testnorgeSamService;

    private ResponseEntity expectedResponse = ResponseEntity.ok().build();

    @Test
    public void shouldGenerereSamordningsmeldinger() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;
        var syntetiserSamRequest = new SyntetiserSamRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeSamConsumer.startSyntetisering(syntetiserSamRequest)).thenReturn(expectedResponse);

        var response = testnorgeSamService.genererSamordningsmeldinger(syntetiserSamRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        verify(testnorgeSamConsumer).startSyntetisering(syntetiserSamRequest);
    }
}