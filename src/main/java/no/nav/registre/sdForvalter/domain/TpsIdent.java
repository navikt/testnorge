package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.database.model.TpsIdentModel;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class TpsIdent extends FasteData {

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

    public TpsIdent(TpsIdentModel model) {
        super(model);
        fnr = model.getFnr();
        firstName = model.getFirstName();
        lastName = model.getLastName();
        address = model.getAddress();
        postNr = model.getPostNr();
        city = model.getCity();
    }
}
