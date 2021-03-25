package no.nav.adresse.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdresseRequest {

    private String veinavn;
    private String postnummer;
    private String kommunenummer;
    private String bydelsnummer;
}
