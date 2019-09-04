package no.nav.registre.udistub.core.service.to;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.udistub.core.service.to.opphold.UdiOppholdStatus;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

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

    @JsonManagedReference
    private List<UdiAvgjorelse> avgjoerelser;

    @JsonManagedReference
    private List<UdiAlias> aliaser;

    @JsonManagedReference
    private UdiArbeidsadgang arbeidsadgang;

    @JsonManagedReference
    private UdiOppholdStatus oppholdStatus;

    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;
    private JaNeiUavklart soeknadOmBeskyttelseUnderBehandling;

    private LocalDate soknadDato;
}
