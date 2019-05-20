package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsPdlKontaktinformasjonForDoedsbo {

    private String adresselinje1;
    private String adresselinje2;
    private PdlOrganisasjon advokatSomAdressat;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
    private PdlKontaktpersonMedIdNummer kontaktpersonMedIdNummerSomAdressat;
    private RsPdlKontaktpersonUtenIdNummerSomAdressat kontaktpersonUtenIdNummerSomAdressat;
    private String landkode;
    private PdlOrganisasjon organisasjonSomAdressat;
    private String postnummer;
    private String poststedsnavn;
    private PdlSkifteform skifteform;
    private LocalDateTime utstedtDato;
}
