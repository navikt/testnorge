package no.nav.registre.spion.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @JsonProperty("gate")
    private String street;
    @JsonProperty("postnummer")
    private String postal;
    @JsonProperty("by")
    private String city;
    @JsonProperty("land")
    private String country;

}
