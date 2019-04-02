package no.nav.dolly.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.dolly.aareg.AaregRestConsumer;
import no.nav.dolly.aareg.AaregWsConsumer;
import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;

@RunWith(MockitoJUnitRunner.class)
public class AaregControllerTest {

    private static Map<String, String> status = new HashMap<>();

    static {
        status.put("t0", "OK");
    }

    @Mock
    private AaregWsConsumer aaregWsConsumer;

    @Mock
    private AaregRestConsumer aaregRestConsumer;

    @InjectMocks
    private AaregController aaregController;

    @Test
    public void opprettArbeidsforhold_OK() {

        when(aaregWsConsumer.opprettArbeidsforhold(any(RsAaregOpprettRequest.class))).thenReturn(status);

        RsAaregResponse response = aaregController.opprettArbeidsforhold(new RsAaregOpprettRequest());

        assertThat(response.getStatusPerMiljoe().get("t0"), is(equalTo("OK")));
        verify(aaregWsConsumer).opprettArbeidsforhold(any(RsAaregOpprettRequest.class));
    }

    @Test
    public void oppdaterArbeidsforhold_OK() {

        when(aaregWsConsumer.oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class))).thenReturn(status);

        RsAaregResponse response = aaregController.oppdaterArbeidsforhold(new RsAaregOppdaterRequest());

        assertThat(response.getStatusPerMiljoe().get("t0"), is(equalTo("OK")));
        verify(aaregWsConsumer).oppdaterArbeidsforhold(any(RsAaregOppdaterRequest.class));
    }

    @Test
    public void lesArbeidsforhold_OK() {

        when(aaregRestConsumer.readArbeidsforhold(anyString(), anyString())).thenReturn(new ResponseEntity("Innhold fra tjenesten", HttpStatus.OK));

        aaregController.lesArbeidsforhold(anyString(), anyString());

        verify(aaregRestConsumer).readArbeidsforhold(anyString(), anyString());
    }
}