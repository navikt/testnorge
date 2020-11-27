package no.nav.registre.testnorge.personexportapi.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EndringsmeldingDTO {

    private static final String AARSAKSKODE_FOEDSEL = "01";
    private static final String AARSAKSKODE_INNVANDRING = "02";

    String fodselsdato;
    String personnummer;
    String fornavn;
    String mellomnavn;
    String slektsnavn;
    String statuskode;
    String aarsakskode;
    String personkode;
    String kommunenummer;
    String postnummer;
    String flyttedatoAdr; // flyttedato
    String adressetype; // O = Offisiell, dvs gateadresse, M = matrikkeladresse
    String gateGaard;  // gatekode (O) eller gårdsnummer (M)
    String adressenavn; // gatenavn (O) eller gårdsnavn (M)
    String husBruk;     // husnummer (O) eller bruksnummer (M)
    String bokstavFestenr;  // husbokstav (O) eller festenummer (M)
    String bolignr;    // eks H0101
    String tilleggsadresse;
    String adresse1;
    String adresse2;
    String adresse3;
    String postadrLand;
    String sivilstand;
    String regdatoSivilstand;
    String statsborgerskap;
    String regdatoStatsb;
    String innvandretFraLand;
    String fraLandFlyttedato;
    String utvandretTilLand;
    String tilLandFlyttedato;
    String spesRegType;
    String datoSpesRegType;

    @JsonIgnore
    public boolean isFoedsel() {
        return AARSAKSKODE_FOEDSEL.equals(aarsakskode);
    }

    @JsonIgnore
    public boolean isInnvandring() {
        return AARSAKSKODE_INNVANDRING.equals(aarsakskode);
    }

    @JsonIgnore
    public boolean isMatrikkeladresse() {
        return "M".equals(getAdressetype());
    }

    @JsonIgnore
    public boolean isGateadresse() {
        return "O".equals(getAdressetype());
    }
}