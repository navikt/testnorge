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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.SamSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;

@RunWith(MockitoJUnitRunner.class)
public class SamSyntPakkenServiceTest {

    @Mock
    private SamSyntConsumer samSyntConsumer;

    @InjectMocks
    private SamSyntPakkenService samSyntPakkenService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> expectedIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr2));
    private ResponseEntity expectedResponse = ResponseEntity.ok().build();

    @Test
    public void shouldGenerereSamordningsmeldinger() {
        SyntetiserSamRequest syntetiserSamRequest = new SyntetiserSamRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(samSyntConsumer.startSyntetisering(syntetiserSamRequest)).thenReturn(expectedResponse);

        ResponseEntity response = samSyntPakkenService.genererSamordningsmeldinger(syntetiserSamRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        verify(samSyntConsumer).startSyntetisering(syntetiserSamRequest);
    }
}