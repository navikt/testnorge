package no.nav.dolly.appservices.tpsf.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import no.nav.dolly.bestilling.sigrunstub.SigrunstubResponseHandler;

@RunWith(MockitoJUnitRunner.class)
public class SigrunStubResponseHandlerTest {

    @InjectMocks
    private SigrunstubResponseHandler responseHandler;

    @Test
    public void extractResponse_SjekkAtHttpOkReturnererOk() {
        ResponseEntity<String> response = ResponseEntity.ok().body("[200,200]");
        String progressUpdate = responseHandler.extractResponse(response);
        assertThat(progressUpdate, is("OK"));
    }

    @Test
    public void extractResponse_SjekkAtAnnetEnnHttpOkGirFeil() {
        ResponseEntity<String> response = ResponseEntity.ok().body("[200,400]");
        String progressUpdate = responseHandler.extractResponse(response);
        assertThat(progressUpdate, is("FAIL"));
    }
}
