package no.nav.udistub.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

import java.time.LocalDate;
import java.util.List;

import no.nav.udistub.service.dto.opphold.UdiOppholdStatus;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UdiPerson {

    private String ident;
    private UdiPersonNavn navn;
    private LocalDate foedselsDato;

    private List<UdiAvgjorelse> avgjoerelser;

    private List<UdiAlias> aliaser;

    private UdiArbeidsadgang arbeidsadgang;
    private UdiArbeidsadgangUtvidet arbeidsadgangUtvidet;
    private UdiOppholdStatus oppholdStatus;

    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;
    private JaNeiUavklart soeknadOmBeskyttelseUnderBehandling;

    private LocalDate soknadDato;
}
