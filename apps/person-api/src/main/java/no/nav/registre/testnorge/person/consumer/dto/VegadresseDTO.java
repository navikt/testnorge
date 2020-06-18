package no.nav.registre.testnorge.person.consumer.dto;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.person.domain.Adresse;

public class VegadresseDTO {

    public VegadresseDTO (Adresse adresse) {
        adressenavn = splitGatenavn(adresse.getGatenavn()).get(0);
        husnummer = splitGatenavn(adresse.getGatenavn()).get(1);
        postnummer = adresse.getPostnummer();
        kommunenummer = adresse.getKommunenummer();
    }
    String adressekode;
    String adressenavn;
    String adresseTilleggsnavn;
    String bruksenhetsnummer;
    String bruksenhetstype;
    String husbokstav;
    String husnummer;
    String postnummer;
    String kommunenummer;

    private List<String> splitGatenavn ( String gatenavn) {
        return Arrays.asList(gatenavn.split("^\\d+$", 2));
    }
}
