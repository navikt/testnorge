package no.nav.dolly.bestilling.arbeidsplassencv.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class ArbeidsplassenCVStatusDTO {

    private HttpStatus status;
    private String feilmelding;
    private ArbeidsplassenCVDTO arbeidsplassenCV;

    private JsonNode jsonNode;
}
