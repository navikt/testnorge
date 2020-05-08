package no.nav.registre.sdForvalter.dto.organisasjon.v1;

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
    private String adresselinje1;
    @JsonProperty
    private String adresselinje2;
    @JsonProperty
    private String adresselinje3;
    @JsonProperty
    private String postnr;
    @JsonProperty
    private String kommunenr;
    @JsonProperty(required = true)
    private String landkode;
    @JsonProperty
    private String poststed;
}
