package no.nav.registre.sdforvalter.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import no.nav.registre.sdforvalter.domain.TpsIdent;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class SkdRequest {

    @NonNull
    @JsonProperty("foedselsdato")
    private String dateOfBirth;
    @NonNull
    @JsonProperty("personnummer")
    private String personnummer;
    @JsonProperty("fornavn")
    private String firstName;
    @JsonProperty("etternavn")
    private String lastName;
    @JsonProperty("adresse")
    private String address;
    @JsonProperty("postnr")
    private String postnr;
    @JsonProperty("by")
    private String city;

    public SkdRequest(TpsIdent ident){
        personnummer = ident.getFnr().substring(6);
        dateOfBirth = ident.getFnr().substring(0, 6);
        address = ident.getAddress();
        city = ident.getCity();
        firstName = ident.getFirstName();
        lastName = ident.getLastName();
        postnr = ident.getPostNr();
    }

}
