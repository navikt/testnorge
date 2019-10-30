package no.nav.registre.aareg.provider.rs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.registre.aareg.consumer.rs.TpsfConsumer;
import no.nav.registre.aareg.consumer.rs.responses.MiljoerResponse;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.service.AaregService;

@RunWith(MockitoJUnitRunner.class)
public class ArbeidsforholdControllerTest {

    @Mock
    private AaregService aaregService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private ArbeidsforholdController arbeidsforholdController;

    private final String ident = "01010101010";
    private final String miljoe = "t0";

    @Test
    public void shouldOppretteArbeidsforhold() {
        RsAaregOpprettRequest opprettRequest = RsAaregOpprettRequest.builder().build();
        arbeidsforholdController.opprettArbeidsforhold(opprettRequest);
        verify(aaregService).opprettArbeidsforhold(opprettRequest);
    }

    @Test
    public void shouldOppdatereArbeidsforhold() {
        RsAaregOppdaterRequest oppdaterRequest = new RsAaregOppdaterRequest();
        arbeidsforholdController.oppdaterArbeidsforhold(oppdaterRequest);
        verify(aaregService).oppdaterArbeidsforhold(oppdaterRequest);
    }

    @Test
    public void shouldHenteArbeidsforhold() {
        arbeidsforholdController.hentArbeidsforhold(ident, miljoe);
        verify(aaregService).hentArbeidsforhold(ident, miljoe);
    }

    @Test
    public void shouldSletteArbeidsforholdFraEttMiljoe() {
        List<String> miljoer = Collections.singletonList(miljoe);
        arbeidsforholdController.slettArbeidsforhold(ident, miljoer);
        verify(aaregService).slettArbeidsforhold(ident, miljoer);
    }

    @Test
    public void shouldSletteArbeidsforholdFraAlleMiljoer() {
        List<String> miljoer = Arrays.asList("t0", "t1", "t2");
        MiljoerResponse miljoerResponse = new MiljoerResponse();
        miljoerResponse.setEnvironments(miljoer);
        when(tpsfConsumer.hentMiljoer()).thenReturn(ResponseEntity.ok(miljoerResponse));

        arbeidsforholdController.slettArbeidsforhold(ident, null);

        verify(tpsfConsumer).hentMiljoer();
        verify(aaregService).slettArbeidsforhold(ident, miljoer);
    }
}