package no.nav.dolly.bestilling.sigrunstub;

import static java.util.Objects.nonNull;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SigrunStubResponseHandler {

    public String extractResponse(ResponseEntity<Object> response) {
        return nonNull(response) && nonNull(response.getBody()) && !response.getBody().toString().contains("400") ? "OK" : unWrapError(response);
    }

    private String unWrapError(ResponseEntity<Object> response) {
        log.error("Kall til Sigrun-stub feilet, response={}", nonNull(response) ? response.getBody() : "");
        return "FEIL";
    }
}