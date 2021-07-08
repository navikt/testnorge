package no.nav.registre.sdforvalter.domain.status.ereg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.testnav.libs.dto.organisasjon.v1.AdresseDTO;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Adresse {
    private final String kommunenummer;

    public Adresse(no.nav.registre.sdforvalter.domain.Adresse adresse){
        kommunenummer = adresse.getKommunenr();
    }

    public Adresse(AdresseDTO adresseDTO){
        this.kommunenummer = adresseDTO.getKommunenummer();
    }

}
