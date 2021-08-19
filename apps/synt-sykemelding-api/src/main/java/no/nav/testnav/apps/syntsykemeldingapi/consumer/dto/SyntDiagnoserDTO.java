package no.nav.testnav.apps.syntsykemeldingapi.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SyntDiagnoserDTO {
    @JsonProperty("dn")
    String diagnose;
    @JsonProperty("s")
    String system;
    @JsonProperty("v")
    String diagnosekode;
}
