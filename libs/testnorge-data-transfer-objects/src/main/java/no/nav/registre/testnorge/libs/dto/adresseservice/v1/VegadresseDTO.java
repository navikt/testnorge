package no.nav.registre.testnorge.libs.dto.adresseservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VegadresseDTO {

    private String matrikkelId;
    private String adressekode;
    private String adressenavn;
    private Integer husnummer;
    private String husbokstav;
    private String postnummer;
    private String poststed;
    private String kommunenummer;
    private String kommunenavn;
    private String bydelsnummer;
    private String bydelsnavn;
    private String tilleggsnavn;
    private String fylkesnummer;
    private String fylkesnavn;
}
