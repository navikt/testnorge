package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlKontaktinformasjonForDoedsbo {

    private Adressat adressat;
    private String adresselinje1;
    private String adresselinje2;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
    private String kilde;
    private String landkode;
    private String postnummer;
    private String poststedsnavn;
    private PdlSkifteform skifteform;
    private LocalDate utstedtDato;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adressat {

        private PdlAdvokat advokatSomAdressat;
        private PdlKontaktpersonMedIdNummer kontaktpersonMedIdNummerSomAdressat;
        private PdlKontaktpersonUtenIdNummer kontaktpersonUtenIdNummerSomAdressat;
        private PdlOrganisasjon organisasjonSomAdressat;
    }
}
