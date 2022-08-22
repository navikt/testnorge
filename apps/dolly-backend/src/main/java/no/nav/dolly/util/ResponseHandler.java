package no.nav.dolly.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Component
public class ResponseHandler {

    private static boolean isOkStatus(ResponseEntity<Object> response) {
        return HttpStatus.OK == response.getStatusCode() ||
                HttpStatus.CREATED == response.getStatusCode() ||
                HttpStatus.ACCEPTED == response.getStatusCode();
    }

    public String extractResponse(ResponseEntity<Object> response) {
        return nonNull(response) && isOkStatus(response) ? "OK" : unWrapError(response);
    }

    private String unWrapError(ResponseEntity<Object> response) {

        return format("FEIL%s", nonNull(response) && nonNull(response.getBody()) ?
                format(": %s (%s -- %s)", response.getBody(), response.getStatusCodeValue(), response.getStatusCode().getReasonPhrase()) : "");
    }
}