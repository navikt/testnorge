package no.nav.registre.spion.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Address {

    @JsonProperty("gate")
    private final String street;
    @JsonProperty("postnummer")
    private final String postal;
    @JsonProperty("by")
    private final String city;
    @JsonProperty("land")
    private final String country;

}
