package no.nav.registre.testnorge.organisasjon.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDetaljerDTO {
    @JsonProperty
    private final List<AdresseDTO> forretningsadresser;
    @JsonProperty
    private final List<AdresseDTO> postadresser;
}
