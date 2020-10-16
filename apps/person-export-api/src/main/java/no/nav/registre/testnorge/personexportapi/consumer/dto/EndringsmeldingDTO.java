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

    @JsonIgnore
    public boolean isFoedsel() {
        return aarsakskode != null && aarsakskode.equals(AARSAKSKODE_FOEDSEL);
    }

    @JsonIgnore
    public boolean isInnvandring() {
        return aarsakskode != null && aarsakskode.equals(AARSAKSKODE_INNVANDRING);
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