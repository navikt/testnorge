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

import no.nav.registre.orkestratoren.consumer.rs.PoppSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@RunWith(MockitoJUnitRunner.class)
public class PoppSyntPakkenServiceTest {

    @Mock
    private PoppSyntConsumer poppSyntConsumer;

    @InjectMocks
    private PoppSyntPakkenService poppSyntPakkenService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String testdataEier = "test";
    private ResponseEntity expectedResponse = ResponseEntity.ok().build();

    @Test
    public void shouldGenerereSkattegrunnlag() {
        SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(poppSyntConsumer.startSyntetisering(syntetiserPoppRequest, testdataEier)).thenReturn(expectedResponse);

        ResponseEntity response = poppSyntPakkenService.genererSkattegrunnlag(syntetiserPoppRequest, testdataEier);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        verify(poppSyntConsumer).startSyntetisering(syntetiserPoppRequest, testdataEier);
    }
}