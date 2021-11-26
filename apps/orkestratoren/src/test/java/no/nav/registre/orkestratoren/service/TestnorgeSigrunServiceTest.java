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

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSigrunConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@ExtendWith(MockitoExtension.class)
public class TestnorgeSigrunServiceTest {

    @Mock
    private TestnorgeSigrunConsumer testnorgeSigrunConsumer;

    @InjectMocks
    private TestnorgeSigrunService testnorgeSigrunService;

    @Test
    public void shouldGenerereSkattegrunnlag() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;
        var testdataEier = "test";
        var expectedResponse = ResponseEntity.ok().build();
        var syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeSigrunConsumer.startSyntetisering(syntetiserPoppRequest, testdataEier)).thenReturn(expectedResponse);

        var response = testnorgeSigrunService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        verify(testnorgeSigrunConsumer).startSyntetisering(syntetiserPoppRequest, testdataEier);
    }
}