package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.organisasjon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDetaljerDTO {
    @JsonProperty
    List<AdresseDTO> forretningsadresser;
    @JsonProperty
    List<AdresseDTO> postadresser;
}