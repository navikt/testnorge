package no.nav.dolly.domain.resultset.udistub.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdStatusTo;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PersonTo {
    private String ident;
    private PersonNavnTo navn;
    private LocalDate foedselsDato;
    private List<AvgjorelseTo> avgjoerelser;
    private List<AliasTo> aliaser;
    private ArbeidsadgangTo arbeidsadgang;
    private OppholdStatusTo oppholdStatus;
    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;
    private String soeknadOmBeskyttelseUnderBehandling;
    private LocalDate soknadDato;
}
