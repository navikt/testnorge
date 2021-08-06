package no.nav.testnav.libs.domain.dto.skd.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeldingDTO {
    @JsonProperty
    private final String foedselsdato;
    @JsonProperty
    private final String personnummer;
    @JsonProperty
    private final String fornavn;
    @JsonProperty
    private final String etternavn;
    @JsonProperty
    private final String adresse;
    @JsonProperty
    private final String postnr;
    @JsonProperty
    private final String by;
}