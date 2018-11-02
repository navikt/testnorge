package no.nav.dolly.appservices.tpsf.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import no.nav.dolly.domain.jpa.BestillingProgress;

public class SigrunResponseHandlerTest {

    private SigrunResponseHandler sigrunResponseHandler = new SigrunResponseHandler();
    private BestillingProgress progress = new BestillingProgress();

    @Test
    public void extractResponse_SjekkAtHttpOkReturnererOk() {
        ResponseEntity<String> response = ResponseEntity.ok().body("[200,200]");
        String progressUpdate = sigrunResponseHandler.extractResponse(response);
        assertThat(progressUpdate, is("Ok"));
    }

    @Test
    public void extractResponse_SjekkAtAnnetEnnHttpOkGirFeil() {
        ResponseEntity<String> response = ResponseEntity.ok().body("[200,400]");
        String progressUpdate = sigrunResponseHandler.extractResponse(response);
        assertThat(progressUpdate, not("Ok"));
    }

}
