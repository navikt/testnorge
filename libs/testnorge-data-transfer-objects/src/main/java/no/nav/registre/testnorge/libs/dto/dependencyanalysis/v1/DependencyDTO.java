package no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DependencyDTO {

    @JsonProperty(required = true)
    private final String name;
    @JsonProperty(required = true)
    private final Boolean external;
}
