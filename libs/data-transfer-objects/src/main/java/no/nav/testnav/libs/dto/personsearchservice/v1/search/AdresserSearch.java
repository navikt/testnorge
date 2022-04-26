package no.nav.testnav.libs.dto.personsearchservice.v1.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresserSearch {
    BostedsadresseSearch bostedsadresse;
    KontaktadresseSearch kontaktadresse;
    OppholdsadresseSearch oppholdsadresse;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class BostedsadresseSearch {
        String kommunenummer;
        String postnummer;
    }

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class KontaktadresseSearch {
        Boolean norskAdresse;
        Boolean utenlandskAdresse;
        Boolean kontaktadresseForDoedsbo;
    }

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class OppholdsadresseSearch {
        Boolean norskAdresse;
        Boolean utenlandskAdresse;
        String oppholdAnnetSted;
    }
}
