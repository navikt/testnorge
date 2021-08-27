package no.nav.testnav.libs.dto.organiasjonbestilling.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OrderDTO {
    @JsonProperty
    Long id;
    @JsonProperty
    Long queueId;
    @JsonProperty
    Long buildId;
    @JsonProperty
    Long batchId;
    @JsonProperty
    String miljo;
    @JsonProperty
    String uuid;
}
