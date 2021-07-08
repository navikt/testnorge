package no.nav.testnav.libs.dto.organisasjonfastedataservice.v1;

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

    @JsonProperty(required = true)
    String adresselinje1;
    @JsonProperty
    String adresselinje2;
    @JsonProperty
    String adresselinje3;
    @JsonProperty
    String postnr;
    @JsonProperty
    String kommunenr;
    @JsonProperty(required = true)
    String landkode;
    @JsonProperty
    String poststed;
}
