package no.nav.dolly.domain.resultset.udistub.model.opphold;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiOppholdSammeVilkaar {

    private LocalDate oppholdSammeVilkaarEffektuering;
    private UdiPeriode oppholdSammeVilkaarPeriode;
    private UdiOppholdstillatelseType oppholdstillatelseType;
    private LocalDate oppholdstillatelseVedtaksDato;
}