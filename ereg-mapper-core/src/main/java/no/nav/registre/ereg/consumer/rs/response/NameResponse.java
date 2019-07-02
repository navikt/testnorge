package no.nav.registre.ereg.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NameResponse {

    @JsonProperty("fornavn")
    private String firstName;
    @JsonProperty("etternavn")
    private String lastName;

    public String toString() {
        return firstName + " " + lastName;
    }

}
