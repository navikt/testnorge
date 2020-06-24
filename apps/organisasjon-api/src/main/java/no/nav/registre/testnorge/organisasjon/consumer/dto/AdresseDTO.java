package no.nav.registre.testnorge.organisasjon.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AdresseDTO {
    @JsonProperty
    private String adresselinje1;
    @JsonProperty
    private String adresselinje2;
    @JsonProperty
    private String adresselinje3;
    @JsonProperty
    private String kommunenummer;
    @JsonProperty
    private String landkode;
    @JsonProperty
    private String postnummer;
    @JsonProperty
    private String poststed;
}
