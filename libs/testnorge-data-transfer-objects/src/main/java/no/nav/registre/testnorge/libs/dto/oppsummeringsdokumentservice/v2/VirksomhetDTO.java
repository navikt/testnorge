package no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class VirksomhetDTO {
    @JsonProperty(required = true)
    private String organisajonsnummer;
    @JsonProperty(required = true)
    private List<PersonDTO> personer;
}
