package no.nav.registre.testnorge.libs.dto.statistikkservice.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StatistikkDTO {
    @JsonProperty(required = true)
    StatistikkType type;
    @JsonProperty
    String description;
    @JsonProperty(required = true)
    Double value;
    @JsonProperty(required = true)
    StatistikkValueType valueType;
}
