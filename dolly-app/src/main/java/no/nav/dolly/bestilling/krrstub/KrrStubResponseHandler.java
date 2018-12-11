package no.nav.dolly.bestilling.krrstub;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KrrStubResponseHandler {

    public String extractResponse(ResponseEntity<String> response) {
        return response != null && isOkStatus(response) ? "OK" : "FAIL";
    }

    private boolean isOkStatus(ResponseEntity<String> response) {
        return HttpStatus.OK == response.getStatusCode() ||
                HttpStatus.CREATED == response.getStatusCode() ||
                HttpStatus.ACCEPTED == response.getStatusCode();
    }
}