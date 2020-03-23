package no.nav.registre.aareg.provider.rs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import no.nav.registre.aareg.consumer.rs.TpsfConsumer;
import no.nav.registre.aareg.consumer.rs.response.MiljoerResponse;
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
    private String navCallId = "test";

    @Test
    public void shouldOppretteArbeidsforhold() {
        var opprettRequest = RsAaregOpprettRequest.builder().build();
        arbeidsforholdController.opprettArbeidsforhold(navCallId, opprettRequest);
        verify(aaregService).opprettArbeidsforhold(opprettRequest);
    }

    @Test
    public void shouldOppdatereArbeidsforhold() {
        var oppdaterRequest = new RsAaregOppdaterRequest();
        arbeidsforholdController.oppdaterArbeidsforhold(navCallId, oppdaterRequest);
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
        arbeidsforholdController.slettArbeidsforhold(navCallId, ident, miljoer);
        verify(aaregService).slettArbeidsforhold(ident, miljoer, navCallId);
    }

    @Test
    public void shouldSletteArbeidsforholdFraAlleMiljoer() {
        var miljoer = Arrays.asList("t0", "t1", "t2");
        var miljoerResponse = new MiljoerResponse();
        miljoerResponse.setEnvironments(miljoer);
        when(tpsfConsumer.hentMiljoer()).thenReturn(miljoerResponse);

        arbeidsforholdController.slettArbeidsforhold(navCallId, ident, null);

        verify(tpsfConsumer).hentMiljoer();
        verify(aaregService).slettArbeidsforhold(ident, miljoer, navCallId);
    }
}