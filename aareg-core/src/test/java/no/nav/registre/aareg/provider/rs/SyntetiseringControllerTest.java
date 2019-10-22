package no.nav.registre.aareg.provider.rs;

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

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.aareg.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private SyntetiserAaregRequest syntetiserAaregRequest;
    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallMeldinger = 2;
    private List<RsAaregResponse> arbeidsforhold;

    @Before
    public void setUp() {
        syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallMeldinger);
        arbeidsforhold = new ArrayList<>();
        arbeidsforhold.add(new RsAaregResponse());
    }

    @Test
    public void shouldStartSyntetisering() {
        when(syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest)).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        ResponseEntity result = syntetiseringController.genererArbeidsforholdsmeldinger(syntetiserAaregRequest);

        verify(syntetiseringService).opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }
}
