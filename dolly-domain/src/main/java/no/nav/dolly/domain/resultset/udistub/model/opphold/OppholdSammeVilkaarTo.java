package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.PeriodeTo;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OppholdSammeVilkaarTo {

    private PeriodeTo oppholdSammeVilkaarPeriode;
    private LocalDate oppholdSammeVilkaarEffektuering;
    private String oppholdstillatelseType;
    private LocalDate oppholdstillatelseVedtaksDato;
}