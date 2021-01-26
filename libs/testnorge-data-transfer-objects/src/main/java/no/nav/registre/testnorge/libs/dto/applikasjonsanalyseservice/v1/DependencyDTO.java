package no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DependencyDTO {
    String name;
    String cluster;
    String namespace;
}
