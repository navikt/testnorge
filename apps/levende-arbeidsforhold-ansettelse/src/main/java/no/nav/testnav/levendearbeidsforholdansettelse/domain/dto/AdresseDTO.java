package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Builder
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
