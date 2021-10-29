package no.nav.testnav.apps.adresseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
}
