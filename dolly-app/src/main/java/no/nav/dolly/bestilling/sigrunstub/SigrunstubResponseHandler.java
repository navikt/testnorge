package no.nav.dolly.bestilling.sigrunstub;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SigrunstubResponseHandler {

    public String extractResponse(ResponseEntity<String> response) {
        return response != null && response.getBody() != null && !response.getBody().contains("400") ? "OK" : "FAIL";
    }
}