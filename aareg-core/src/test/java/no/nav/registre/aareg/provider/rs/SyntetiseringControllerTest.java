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

import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
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
    private Boolean lagreIAareg = false;
    private boolean fyllUtArbeidsforhold = true;
    private List<ArbeidsforholdsResponse> arbeidsforhold;

    @Before
    public void setUp() {
        syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallMeldinger);
        arbeidsforhold = new ArrayList<>();
        arbeidsforhold.add(new ArbeidsforholdsResponse());
    }

    @Test
    public void shouldStartSyntetisering() {
        when(syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, lagreIAareg)).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        ResponseEntity result = syntetiseringController.genererArbeidsforholdsmeldinger(lagreIAareg, syntetiserAaregRequest);

        verify(syntetiseringService).opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, lagreIAareg);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldSendArbeidsforholdTilAaregstub() {
        syntetiseringController.sendArbeidsforholdTilAareg(fyllUtArbeidsforhold, arbeidsforhold);

        verify(syntetiseringService).sendArbeidsforholdTilAareg(arbeidsforhold, fyllUtArbeidsforhold);
    }
}
