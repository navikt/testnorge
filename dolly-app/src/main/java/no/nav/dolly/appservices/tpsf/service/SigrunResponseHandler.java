package no.nav.dolly.appservices.tpsf.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SigrunResponseHandler {
    private static final int REQUEST_OK = 200;
    private static final int REQUEST_CREATED = 201;

    public String extractResponse(ResponseEntity<String> response) {
        if (response.getStatusCodeValue() == REQUEST_CREATED || response.getStatusCodeValue() == REQUEST_OK) {
            return "Ok";
        } else {
            log.error("{} failed", response.toString());
            return "Failed";
        }
    }
}
