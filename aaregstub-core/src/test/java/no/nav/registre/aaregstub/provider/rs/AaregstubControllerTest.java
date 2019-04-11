package no.nav.registre.aaregstub.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aaregstub.arbeidsforhold.ArbeidsforholdsResponse;
import no.nav.registre.aaregstub.service.ArbeidsforholdService;

@RunWith(MockitoJUnitRunner.class)
public class AaregstubControllerTest {

    @Mock
    private ArbeidsforholdService arbeidsforholdService;

    @InjectMocks
    private AaregstubController aaregstubController;

    private Long id = 2L;
    private String fnr = "01010101010";
    private List<ArbeidsforholdsResponse> arbeidsforholdsmeldinger;
    private Boolean lagreIAareg = false;

    @Before
    public void setUp() {
        arbeidsforholdsmeldinger = new ArrayList<>();
    }

    @Test
    public void shouldSaveArbeidsforhold() {
        aaregstubController.lagreArbeidsforhold(lagreIAareg, arbeidsforholdsmeldinger);
        verify(arbeidsforholdService).lagreArbeidsforhold(arbeidsforholdsmeldinger, lagreIAareg);
    }

    @Test
    public void shouldGetArbeidsforhold() {
        aaregstubController.hentArbeidsforhold(id);
        verify(arbeidsforholdService).hentArbeidsforhold(id);
    }

    @Test
    public void shouldDeleteArbeidsforhold() {
        aaregstubController.slettArbeidsforhold(id);
        verify(arbeidsforholdService).slettArbeidsforhold(id);
    }

    @Test
    public void shouldGetIdentMedArbeidsforhold() {
        aaregstubController.hentIdentMedArbeidsforhold(fnr);
        verify(arbeidsforholdService).hentIdentMedArbeidsforholdNy(fnr);
    }

    @Test
    public void shouldGetAlleArbeidstakere() {
        aaregstubController.hentAlleArbeidstakere();
        verify(arbeidsforholdService).hentAlleArbeidstakere();
    }
}
