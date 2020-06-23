package no.nav.registre.sdforvalter.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EregAdresse {
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
