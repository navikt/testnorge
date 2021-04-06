package no.nav.registre.testnorge.organisasjonservice.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AdresseDTO {
    @JsonProperty
    String adresselinje1;
    @JsonProperty
    String adresselinje2;
    @JsonProperty
    String adresselinje3;
    @JsonProperty
    String kommunenummer;
    @JsonProperty
    String landkode;
    @JsonProperty
    String postnummer;
    @JsonProperty
    String poststed;
}
