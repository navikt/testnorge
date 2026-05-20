package no.nav.testnav.libs.dto.ereg.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EregServicesResponse {

    private String error;

    private String miljoe;
    private JsonNode organisasjon;
}
