package no.nav.testnav.libs.dto.ameldingservice.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AMeldingDTO {
    @JsonProperty(required = true)
    private LocalDate kalendermaaned;
    @JsonProperty(required = true)
    private String opplysningspliktigOrganisajonsnummer;
    @JsonProperty(required = true)
    private List<VirksomhetDTO> virksomheter;
}
