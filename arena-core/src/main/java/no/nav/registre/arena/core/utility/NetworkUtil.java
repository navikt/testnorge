package no.nav.registre.arena.core.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkUtil {
    public static Boolean validRespons(ResponseEntity response) {
        if (response.getBody() == null) {
            log.error("Mistet kontakt. Fikk null response body.");
            return false;
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Status: {}", response.getStatusCode());
            return false;
        }
        return true;
    }
}


