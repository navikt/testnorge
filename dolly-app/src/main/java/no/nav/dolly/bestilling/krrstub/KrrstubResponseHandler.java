package no.nav.dolly.bestilling.krrstub;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KrrstubResponseHandler {

    public String extractResponse(ResponseEntity<String> response) {
        return response != null && HttpStatus.OK == response.getStatusCode() ? "OK" : "FAIL";
    }
}