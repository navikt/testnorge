package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.database.model.TpsModel;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class Tps {

    @JsonProperty
    private final String fnr;
    @JsonProperty("fornavn")
    private final String firstName;
    @JsonProperty("etternavn")
    private final String lastName;
    @JsonProperty("adresse")
    private final String address;
    @JsonProperty("postnr")
    private final String postNr;
    @JsonProperty("by")
    private final String city;
    @JsonProperty
    private final KildeSystem kildeSystem;

    public Tps(TpsModel model) {
        fnr = model.getFnr();
        firstName = model.getFirstName();
        lastName = model.getLastName();
        address = model.getAddress();
        postNr = model.getPostNr();
        city = model.getCity();
        kildeSystem = model.getKildeSystemModel() != null ? new KildeSystem(model.getKildeSystemModel()) : null;
    }
}
