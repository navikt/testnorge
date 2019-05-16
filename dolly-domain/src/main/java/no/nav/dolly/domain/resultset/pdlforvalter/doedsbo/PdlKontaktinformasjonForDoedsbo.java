package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlKontaktinformasjonForDoedsbo {

    private String adresselinje1;
    private String adresselinje2;
    private PdlOrganisasjon advokatSomAdressat;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
    private String kilde;
    private PdlKontaktpersonMedIdNummer kontaktpersonMedIdNummerSomAdressat;
    private PdlKontaktpersonUtenIdNummerSomAdressat kontaktpersonUtenIdNummerSomAdressat;
    private String landkode;
    private PdlOrganisasjon organisasjonSomAdressat;
    private String postnummer;
    private String poststedsnavn;
    private PdlSkifteform skifteform;
    private LocalDate utstedtDato;
}
