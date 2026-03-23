package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EregServicesResponse {

    private String error;

    private String miljoe;
    private JsonNode organisasjon;
}
