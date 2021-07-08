package no.nav.testnav.libs.dto.statiskedataforvalter.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OrganisasjonListeDTO {
    @JsonProperty
    List<OrganisasjonDTO> liste;
}