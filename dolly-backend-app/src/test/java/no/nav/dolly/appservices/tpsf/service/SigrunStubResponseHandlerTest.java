package no.nav.dolly.appservices.tpsf.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import no.nav.dolly.bestilling.sigrunstub.SigrunStubResponseHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class SigrunStubResponseHandlerTest {

    @InjectMocks
    private SigrunStubResponseHandler responseHandler;

    @Test
    public void extractResponse_SjekkAtHttpOkReturnererOk() {
        ResponseEntity<Object> response = ResponseEntity.ok().body("[200,200]");
        String progressUpdate = responseHandler.extractResponse(response);
        assertThat(progressUpdate, is("OK"));
    }

    @Test
    public void extractResponse_SjekkAtAnnetEnnHttpOkGirFeil() {
        ResponseEntity<Object> response = ResponseEntity.badRequest().body("[200,400]");
        String progressUpdate = responseHandler.extractResponse(response);
        assertThat(progressUpdate, is("FEIL"));
    }
}
