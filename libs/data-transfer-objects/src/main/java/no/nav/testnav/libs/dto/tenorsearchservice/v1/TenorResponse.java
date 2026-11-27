package no.nav.testnav.libs.dto.tenorsearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.JsonNode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorResponse {

    private HttpStatus status;
    private JsonNode data;
    private String query;
    private String error;
}