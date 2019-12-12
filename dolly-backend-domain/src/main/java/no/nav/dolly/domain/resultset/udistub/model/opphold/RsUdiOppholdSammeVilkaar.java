package no.nav.dolly.domain.resultset.udistub.model.opphold;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPeriode;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RsUdiOppholdSammeVilkaar {

    private LocalDateTime oppholdSammeVilkaarEffektuering;
    private RsUdiPeriode oppholdSammeVilkaarPeriode;
    private UdiOppholdstillatelseType oppholdstillatelseType;
    private LocalDateTime oppholdstillatelseVedtaksDato;
}