package no.nav.dolly.bestilling.krrstub;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KrrStubResponseHandler {

    public String extractResponse(ResponseEntity<Object> response) {
        return nonNull(response) && isOkStatus(response) ? "OK" : unWrapError(response);
    }

    private String unWrapError(ResponseEntity<Object> response) {
        if (isNotNull(response)) {
            String krrFeil = format("%s (%s -- %s)", ((Map) response.getBody()).values().toArray()[0],
                    response.getStatusCodeValue(), response.getStatusCode().getReasonPhrase());
            log.error("Kall til KRR-stub feilet grunnet \"{}\"", krrFeil);
            return format("FEIL: %s", krrFeil);
        }
        return "FEIL";
    }

    private boolean isNotNull(ResponseEntity<Object> response) {
        return nonNull(response) && nonNull(response.getBody()) && !((Map) response.getBody()).values().isEmpty();
    }

    private static boolean isOkStatus(ResponseEntity<Object> response) {
        return HttpStatus.OK == response.getStatusCode() ||
                HttpStatus.CREATED == response.getStatusCode() ||
                HttpStatus.ACCEPTED == response.getStatusCode();
    }
}