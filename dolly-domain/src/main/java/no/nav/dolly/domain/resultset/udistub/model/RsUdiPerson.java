package no.nav.dolly.domain.resultset.udistub.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang.UdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.avgjoerelse.UdiAvgjorelse;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RsUdiPerson {

        private List<UdiAvgjorelse> avgjoerelser;
        private List<UdiAlias> aliaser;
        private UdiArbeidsadgang arbeidsadgang;
        private UdiOppholdStatus oppholdStatus;
        private Boolean avgjoerelseUavklart;
        private Boolean harOppholdsTillatelse;
        private Boolean flyktning;
        private String soeknadOmBeskyttelseUnderBehandling;
        private LocalDate soknadDato;
}
