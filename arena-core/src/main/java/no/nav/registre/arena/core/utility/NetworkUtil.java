package no.nav.registre.arena.core.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkUtil {
    public static Boolean validResponse(ResponseEntity response) {

        if (response.getStatusCode() != HttpStatus.OK) {
            return false;
        }

        return response.getBody() != null;
    }
}


