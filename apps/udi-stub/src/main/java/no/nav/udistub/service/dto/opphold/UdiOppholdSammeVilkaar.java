package no.nav.udistub.service.dto.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.udistub.service.dto.UdiPeriode;
import no.udi.mt_1067_nav_data.v1.OppholdstillatelseKategori;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UdiOppholdSammeVilkaar {

    private UdiPeriode oppholdSammeVilkaarPeriode;
    private LocalDate oppholdSammeVilkaarEffektuering;
    private OppholdstillatelseKategori oppholdstillatelseType;
    private LocalDate oppholdstillatelseVedtaksDato;
}