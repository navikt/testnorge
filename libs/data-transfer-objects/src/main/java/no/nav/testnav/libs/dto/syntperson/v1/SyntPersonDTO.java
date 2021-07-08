package no.nav.testnav.libs.dto.syntperson.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SyntPersonDTO {
    @JsonProperty
    String slektsnavn;
    @JsonProperty
    String fornavn;
    @JsonProperty
    String adressenavn;
    @JsonProperty
    String postnummer;
    @JsonProperty
    String kommunenummer;
    @JsonProperty
    String fodselsdato;
}