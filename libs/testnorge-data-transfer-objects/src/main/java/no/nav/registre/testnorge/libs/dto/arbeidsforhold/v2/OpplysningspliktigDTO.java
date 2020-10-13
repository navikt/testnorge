package no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OpplysningspliktigDTO {
    @JsonProperty(required = true)
    LocalDate kalendermaaned;
    @JsonProperty(required = true)
    String opplysningspliktigOrganisajonsnummer;
    @JsonProperty(required = true)
    List<VirksomhetDTO> virksomheter;
}
