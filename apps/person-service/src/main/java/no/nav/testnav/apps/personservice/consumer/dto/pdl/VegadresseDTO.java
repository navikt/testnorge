package no.nav.testnav.apps.personservice.consumer.dto.pdl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.testnav.apps.personservice.domain.Adresse;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class VegadresseDTO {
    String adressenavn;
    String husnummer;
    String postnummer;
    String kommunenummer;

    public VegadresseDTO(Adresse adresse) {
        adressenavn = adresse.getAdressenavn();
        husnummer = adresse.getHusnummer();
        postnummer = adresse.getPostnummer();
        kommunenummer = adresse.getKommunenummer();
    }
}
