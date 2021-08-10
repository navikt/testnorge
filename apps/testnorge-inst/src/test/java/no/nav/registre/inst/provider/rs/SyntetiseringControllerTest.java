package no.nav.registre.inst.provider.rs;

import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.service.SyntetiseringService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private String id = "test";

    @Before
    public void setUp() {
        syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallMeldinger);
    }

    @Test
    public void shouldStartSyntetisering() {
        var fnr = "01010101010";
        Map<String, List<OppholdResponse>> expectedResponse = new HashMap<>();
        expectedResponse.put(fnr, Collections.singletonList(OppholdResponse.builder()
                .status(HttpStatus.OK)
                .build()));
        when(syntetiseringService.finnSyntetiserteMeldingerOgLagreIInst2(id, id, miljoe, syntetiserInstRequest)).thenReturn(expectedResponse);

        var result = syntetiseringController.genererInstitusjonsmeldinger(id, id, miljoe, syntetiserInstRequest);

        verify(syntetiseringService).finnSyntetiserteMeldingerOgLagreIInst2(id, id, miljoe, syntetiserInstRequest);

        assertThat(result.get(fnr).get(0).getStatus(), is(HttpStatus.OK));
    }
}
