package no.nav.dolly.appservices.tpsf.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SigrunResponseHandler {

    public String extractResponse(ResponseEntity<String> response) {
        boolean contains_badresponse = !response.getBody().contains("400"); //pga. hacken til sigrun-stub blir hver sigrunReq lagt i en strengliste i body for responsen hver req ga på sitt endepunkt.
        if (contains_badresponse) {
            return "Ok";
        } else {
            log.error("{} failed", response.toString());
            return "Failed";
        }
    }
}