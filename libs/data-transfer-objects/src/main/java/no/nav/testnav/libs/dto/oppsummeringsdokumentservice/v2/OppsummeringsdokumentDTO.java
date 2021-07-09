package no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2;

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
public class OppsummeringsdokumentDTO {
    @JsonProperty(required = true)
    private LocalDate kalendermaaned;
    @JsonProperty(required = true)
    private String opplysningspliktigOrganisajonsnummer;
    @JsonProperty(required = true)
    private List<VirksomhetDTO> virksomheter;
    @JsonProperty(required = true)
    private Long version;
}
