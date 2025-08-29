package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
