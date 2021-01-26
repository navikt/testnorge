package no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApplicationInfoDTO {
    String name;
    String cluster;
    String namespace;
    Set<DependencyDTO> dependencies;
}
