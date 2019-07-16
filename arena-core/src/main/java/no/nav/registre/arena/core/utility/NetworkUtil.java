package no.nav.registre.arena.core.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class NetworkUtil {
    public static Boolean validResponse(ResponseEntity response) {

        if (response.getStatusCode() != HttpStatus.OK) {
            return false;
        }

        return response.getBody() != null;
    }
}


