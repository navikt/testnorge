package no.nav.dolly.bestilling.udistub;

import static java.util.Objects.nonNull;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UdiStubResponseHandler {

	public String extractResponse(ResponseEntity<Object> response) {
		return nonNull(response) && nonNull(response.getBody()) && !response.getBody().toString().contains("400") ? "OK" : "FEIL";
	}

}