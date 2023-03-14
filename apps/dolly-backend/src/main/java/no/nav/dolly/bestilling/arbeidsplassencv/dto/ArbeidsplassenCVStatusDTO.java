package no.nav.dolly.bestilling.arbeidsplassencv.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class ArbeidsplassenCVStatusDTO {

    private HttpStatus status;
    private String feilmelding;
    private PAMCVDTO arbeidsplassenCV;
    private JsonNode jsonNode;
    private String uuid;
}
