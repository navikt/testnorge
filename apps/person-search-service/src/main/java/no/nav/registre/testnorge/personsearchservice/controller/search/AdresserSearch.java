package no.nav.registre.testnorge.personsearchservice.controller.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresserSearch {
    BostedsadresseSearch bostedsadresse;
    KontaktadresseSearch kontaktadresse;

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
    }
}
