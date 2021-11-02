package no.nav.dolly.appservices.tpsf.service;

import no.nav.dolly.bestilling.sigrunstub.SigrunStubResponseHandler;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunResponse;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ExtendWith(MockitoExtension.class)
public class SigrunStubResponseHandlerTest {

    @InjectMocks
    private SigrunStubResponseHandler responseHandler;

    @Test
    public void extractResponse_SjekkAtHttpOkReturnererOk() {
        ResponseEntity<SigrunResponse> response = ResponseEntity.ok().body(SigrunResponse.builder()
                .opprettelseTilbakemeldingsListe(List.of(
                        SigrunResponse.ResponseElement.builder().status(200).build(),
                        SigrunResponse.ResponseElement.builder().status(200).build()))
                .build());
        String progressUpdate = responseHandler.extractResponse(response);
        assertThat(progressUpdate, is("OK"));
    }

    @Test
    public void extractResponse_SjekkAtAnnetEnnHttpOkGirFeil() {
        ResponseEntity<SigrunResponse> response = ResponseEntity.badRequest().body(SigrunResponse.builder()
                .opprettelseTilbakemeldingsListe(List.of(
                        SigrunResponse.ResponseElement.builder().status(400).build(),
                        SigrunResponse.ResponseElement.builder().status(200).build()))
                .build());
        String progressUpdate = responseHandler.extractResponse(response);
        assertThat(progressUpdate, CoreMatchers.containsString("FEIL"));
    }
}
