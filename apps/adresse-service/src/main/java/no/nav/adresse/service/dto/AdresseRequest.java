package no.nav.adresse.service.dto;

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

    private String veinavn;
    private String husnummer;
    private String husbokstav;

    private String postnummer;
    private String kommunenummer;
    private String bydelsnummer;

    private String poststed;
    private String kommunenavn;
    private String bydelsnavn;
    private String tilleggsnavn;

    public boolean isEmpty() {
        return isBlank(getMatrikkelId()) &&
                isBlank(getVeinavn()) &&
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
