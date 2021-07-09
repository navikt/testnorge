package no.nav.testnav.libs.dto.identpool.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FiktiveNavnDTO {
    @JsonProperty
    String fornavn;
    @JsonProperty
    String etternavn;
}
