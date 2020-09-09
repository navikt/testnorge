package no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApplicationDependenciesDTO {
    @JsonProperty(required = true)
    private final String applicationName;
    @JsonProperty(required = true)
    private final Set<DependencyDTO> dependencies;
}
