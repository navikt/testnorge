package no.nav.registre.testnorge.personsearchservice.controller.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BostedSearch {
    String kommunenummer;
    String postnummer;
    Boolean utenlandskAdresse;
}
