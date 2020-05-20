package no.nav.dolly.bestilling.pdlforvalter.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlBostedadresse extends PdlAdresse {

    private LocalDate flyttedato;
    private UkjentBosted ukjentBosted;
    private Matrikkeladresse matrikkeladresse;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UkjentBosted {

        private String bostedskommune;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Matrikkeladresse {

        private String adressetilleggsnavn;
        private String bruksenhetsnummer;
        private Bruksenhetstype bruksenhetstype;
        private Integer bruksnummer;
        private Integer festenummer;
        private Integer gaardsnummer;
        private String kommunenummer;
        private String postnummer;
        private Integer undernummer;
    }
}
