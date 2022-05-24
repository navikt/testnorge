package no.nav.testnav.libs.dto.personsearchservice.v1.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresserSearch {
    BostedsadresseSearch bostedsadresse;
    OppholdsadresseSearch oppholdsadresse;
    String harUtenlandskAdresse;
    String harKontaktadresse;
    String harOppholdsadresse;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class BostedsadresseSearch {
        String borINorge;
        String kommunenummer;
        String postnummer;
        String historiskKommunenummer;
        String historiskPostnummer;
    }

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class OppholdsadresseSearch {
        String oppholdAnnetSted;
    }
}
