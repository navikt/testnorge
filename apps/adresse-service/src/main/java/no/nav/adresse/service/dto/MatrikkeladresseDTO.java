package no.nav.adresse.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatrikkeladresseDTO {

    private String bruksnummer;
    private String gardsnummer;
    private String kommunenummer;
    private String matrikkelId;
    private String postnummer;
    private String poststed;
    private String tilleggsnavn;
}
