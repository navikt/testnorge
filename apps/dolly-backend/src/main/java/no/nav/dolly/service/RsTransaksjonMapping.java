package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
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
