package no.nav.dolly.bestilling.sigrunstub;

import static java.util.Objects.nonNull;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class SigrunStubResponseHandler {

    public String extractResponse(ResponseEntity<Object> response) {
        return nonNull(response) && nonNull(response.getBody()) && !response.getBody().toString().contains("400") ? "OK" : "FEIL";
    }
}