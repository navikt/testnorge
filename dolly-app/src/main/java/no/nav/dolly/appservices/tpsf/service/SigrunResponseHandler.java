package no.nav.dolly.appservices.tpsf.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SigrunResponseHandler {

    public String extractResponse(ResponseEntity<String> response) {
        String responseBody = response.getBody();

        if (responseBody == null || responseBody.contains("400")) {
            return "Failed";
        } else {
            return "Ok";
        }
    }
}