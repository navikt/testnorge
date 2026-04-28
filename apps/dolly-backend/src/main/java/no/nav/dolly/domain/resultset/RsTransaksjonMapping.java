package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTransaksjonMapping {

    private Long id;
    private Long bestillingId;
    private String ident;
    private String system;
    private String miljoe;
    private JsonNode transaksjonId;
    private LocalDateTime datoEndret;
    private String status;
}
