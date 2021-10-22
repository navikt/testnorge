package no.nav.registre.aareg.provider.rs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import no.nav.registre.aareg.consumer.rs.MiljoerConsumer;
import no.nav.registre.aareg.consumer.rs.response.MiljoerResponse;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.service.AaregService;

@ExtendWith(MockitoExtension.class)
public class ArbeidsforholdControllerTest {

    @Mock
    private AaregService aaregService;

    @Mock
    private MiljoerConsumer miljoerConsumer;

    @InjectMocks
    private ArbeidsforholdController arbeidsforholdController;

    private final String ident = "01010101010";
    private final String miljoe = "t0";
    private final String navCallId = "DOLLY";

    @Test
    public void shouldOppretteArbeidsforhold() {
        var opprettRequest = RsAaregOpprettRequest.builder().build();
        arbeidsforholdController.opprettArbeidsforhold(opprettRequest);
        verify(aaregService).opprettArbeidsforhold(opprettRequest);
    }

    @Test
    public void shouldOppdatereArbeidsforhold() {
        var oppdaterRequest = new RsAaregOppdaterRequest();
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
        var miljoer = Collections.singletonList(miljoe);
        arbeidsforholdController.slettArbeidsforhold(ident, miljoer);
        verify(aaregService).slettArbeidsforhold(ident, miljoer, navCallId);
    }

    @Test
    public void shouldSletteArbeidsforholdFraAlleMiljoer() {
        var miljoer = Arrays.asList("t0", "t1", "t2");
        var miljoerResponse = new MiljoerResponse();
        miljoerResponse.setEnvironments(miljoer);
        when(miljoerConsumer.hentMiljoer()).thenReturn(miljoerResponse);

        arbeidsforholdController.slettArbeidsforhold(ident, null);

        verify(miljoerConsumer).hentMiljoer();
        verify(aaregService).slettArbeidsforhold(ident, miljoer, navCallId);
    }
}