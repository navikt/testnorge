package no.nav.adresse.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdresseRequest {

    private String matrikkelId;

    private String adressenavn;
    private String husnummer;
    private String husbokstav;

    private String postnummer;
    private String kommunenummer;
    private String bydelsnummer;

    private String poststed;
    private String kommunenavn;
    private String bydelsnavn;
    private String tilleggsnavn;

    @JsonIgnore
    public boolean isEmpty() {
        return isBlank(getMatrikkelId()) &&
                isBlank(getAdressenavn()) &&
                isBlank(getHusnummer()) &&
                isBlank(getHusbokstav()) &&
                isBlank(getPostnummer()) &&
                isBlank(getKommunenummer()) &&
                isBlank(getBydelsnummer()) &&
                isBlank(getPoststed()) &&
                isBlank(getKommunenavn()) &&
                isBlank(getBydelsnavn()) &&
                isBlank(getTilleggsnavn());
    }
}
