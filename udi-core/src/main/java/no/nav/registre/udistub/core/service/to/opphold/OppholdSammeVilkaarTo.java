package no.nav.registre.udistub.core.service.to.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.udistub.core.service.to.PeriodeTo;
import no.udi.mt_1067_nav_data.v1.OppholdstillatelseKategori;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OppholdSammeVilkaarTo {

    private PeriodeTo oppholdSammeVilkaarPeriode;
    private LocalDate oppholdSammeVilkaarEffektuering;
    private OppholdstillatelseKategori oppholdstillatelseType;
    private LocalDate oppholdstillatelseVedtaksDato;
}