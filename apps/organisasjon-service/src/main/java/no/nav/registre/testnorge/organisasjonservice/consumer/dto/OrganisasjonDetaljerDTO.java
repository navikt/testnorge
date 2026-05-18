package no.nav.registre.testnorge.organisasjonservice.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDetaljerDTO {
    @JsonProperty
    private List<AdresseDTO> forretningsadresser;
    @JsonProperty
    private List<AdresseDTO> postadresser;
}
