package no.nav.dolly.bestilling.sigrunstub;

import no.nav.dolly.bestilling.sigrunstub.dto.SigrunResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class SigrunStubResponseHandler {

    public String extractResponse(ResponseEntity<SigrunResponse> response) {

        if (isNull(response) || !response.hasBody()) {
            return "FEIL";
        }

        return response.getBody().getOpprettelseTilbakemeldingsListe().stream()
                .noneMatch(element -> 200 != element.getStatus()) ? "OK" :
                "FEIL " + response.getBody().getOpprettelseTilbakemeldingsListe().stream()
                        .filter(element -> 200 != element.getStatus())
                        .map(SigrunResponse.ResponseElement::getMessage)
                        .collect(Collectors.joining(", "));
    }
}