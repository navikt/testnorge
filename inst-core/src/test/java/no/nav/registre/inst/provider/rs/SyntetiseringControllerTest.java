package no.nav.registre.inst.provider.rs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private SyntetiserInstRequest syntetiserInstRequest;
    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallMeldinger = 2;

    @Before
    public void setUp() {
        syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallMeldinger);
    }

    @Test
    public void shouldStartSyntetisering() {
        when(syntetiseringService.finnSyntetiserteMeldinger(syntetiserInstRequest)).thenReturn(Arrays.asList(ResponseEntity.status(HttpStatus.OK).build()));

        List<ResponseEntity> result = syntetiseringController.genererInstitusjonsmeldinger(syntetiserInstRequest);

        verify(syntetiseringService).finnSyntetiserteMeldinger(syntetiserInstRequest);

        assertThat(result.get(0).getStatusCode(), is(HttpStatus.OK));
    }
}
