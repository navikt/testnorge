package no.nav.adresse.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MatrikkeladresseRequest {

    private String matrikkelId;

    private String tilleggsnavn;

    private String kommunenummer;
    private String gaardsnummer;
    private String brukesnummer;

    private String postnummer;
    private String poststed;

    @JsonIgnore
    public boolean isEmpty() {
        return isBlank(getMatrikkelId()) &&
                isBlank(getKommunenummer()) &&
                isBlank(getGaardsnummer()) &&
                isBlank(getBrukesnummer()) &&
                isBlank(getPostnummer()) &&
                isBlank(getPoststed()) &&
                isBlank(getTilleggsnavn());
    }
}
