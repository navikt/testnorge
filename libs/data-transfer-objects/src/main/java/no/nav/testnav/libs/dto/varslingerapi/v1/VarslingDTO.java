package no.nav.testnav.libs.dto.varslingerapi.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class VarslingDTO {
    @JsonProperty(required = true)
    String varslingId;
    @JsonProperty
    LocalDate fom;
    @JsonProperty
    LocalDate tom;
}
