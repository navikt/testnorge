package no.nav.testnav.libs.dto.person.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresseDTO {

    @JsonProperty
    String gatenavn;
    @JsonProperty
    String postnummer;
    @JsonProperty
    String poststed;
    @JsonProperty
    String kommunenummer;
}
