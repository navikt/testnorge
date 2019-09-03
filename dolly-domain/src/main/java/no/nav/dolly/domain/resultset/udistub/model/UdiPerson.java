package no.nav.dolly.domain.resultset.udistub.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdStatus;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UdiPerson {
    private String ident;
    private UdiPersonNavn navn;
    private LocalDate foedselsDato;
    private List<UdiAvgjorelse> avgjoerelser;
    private List<UdiAlias> aliaser;
    private UdiArbeidsadgang arbeidsadgang;
    private UdiOppholdStatus udiOppholdStatus;
    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;
    private String soeknadOmBeskyttelseUnderBehandling;
    private LocalDate soknadDato;
}
