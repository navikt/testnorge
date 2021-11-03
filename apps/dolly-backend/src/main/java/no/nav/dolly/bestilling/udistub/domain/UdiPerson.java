package no.nav.dolly.bestilling.udistub.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.UdiHarType;
import no.nav.dolly.domain.resultset.udistub.model.UdiPersonNavn;
import no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang.UdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.opphold.UdiOppholdStatus;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiPerson {

    private List<UdiAlias> aliaser;
    private UdiArbeidsadgang arbeidsadgang;
    private Boolean avgjoerelseUavklart;
    private Boolean flyktning;
    private LocalDate foedselsDato;
    private Boolean harOppholdsTillatelse;
    private String ident;
    private UdiPersonNavn navn;
    private UdiOppholdStatus oppholdStatus;
    private UdiHarType soeknadOmBeskyttelseUnderBehandling;
    private LocalDate soknadDato;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UdiAlias {

        private String fnr;
        private UdiPersonNavn navn;
    }
}
