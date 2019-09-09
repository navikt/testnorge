package no.nav.dolly.domain.resultset.udistub.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang.UdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.avgjoerelse.UdiAvgjorelse;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdStatus;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UdiPerson {

    private List<UdiAlias> aliaser;
    private UdiArbeidsadgang arbeidsadgang;
    private Boolean avgjoerelseUavklart;
    private List<UdiAvgjorelse> avgjoerelser;
    private Boolean flyktning;
    private LocalDate foedselsDato;
    private Boolean harOppholdsTillatelse;
    private String ident;
    private UdiPersonNavn navn;
    private UdiOppholdStatus oppholdStatus;
    private UdiHarType soeknadOmBeskyttelseUnderBehandling;
    private LocalDate soknadDato;
}
