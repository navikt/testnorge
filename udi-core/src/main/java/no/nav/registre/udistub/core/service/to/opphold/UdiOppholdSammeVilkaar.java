package no.nav.registre.udistub.core.service.to.opphold;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.OppholdstillatelseKategori;

import java.time.LocalDate;

import no.nav.registre.udistub.core.service.to.UdiPeriode;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UdiOppholdSammeVilkaar {

    private UdiPeriode oppholdSammeVilkaarPeriode;
    private LocalDate oppholdSammeVilkaarEffektuering;
    private OppholdstillatelseKategori oppholdstillatelseType;
    private LocalDate oppholdstillatelseVedtaksDato;
}