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

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSigrunConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeSigrunServiceTest {

    @Mock
    private TestnorgeSigrunConsumer testnorgeSigrunConsumer;

    @InjectMocks
    private TestnorgeSigrunService testnorgeSigrunService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String testdataEier = "test";
    private ResponseEntity expectedResponse = ResponseEntity.ok().build();

    @Test
    public void shouldGenerereSkattegrunnlag() {
        SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeSigrunConsumer.startSyntetisering(syntetiserPoppRequest, testdataEier)).thenReturn(expectedResponse);

        ResponseEntity response = testnorgeSigrunService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        verify(testnorgeSigrunConsumer).startSyntetisering(syntetiserPoppRequest, testdataEier);
    }
}