package no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record RightDTO(
        @JsonProperty("Id")
        Integer id,
        @JsonProperty("Reportee")
        String reportee,
        @JsonProperty("ServiceCode")
        String serviceCode,
        @JsonProperty("ServiceEditionCode")
        String serviceEditionCode,
        @JsonProperty("Right")
        String right,
        @JsonProperty("ValidTo")
        LocalDateTime validTo
) {
}
