package no.nav.registre.testnorge.libs.dto.ameldingservice.v1;

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
public class VirksomhetDTO {
    @JsonProperty(required = true)
    String organisajonsnummer;
    @JsonProperty(required = true)
    List<PersonDTO> personer;
}
