package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlVegadresse {

    private String adressekode;
    private String adressenavn;
    private String adressetilleggsnavn;
    private String bruksenhetsnummer;
    private PdlAdresse.Bruksenhetstype bruksenhetstype;
    private String husbokstav;
    private String husnummer;
    private String kommunenummer;
    private String postnummer;
}