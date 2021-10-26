package no.nav.dolly.domain.resultset.udistub.model.opphold;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiOppholdStatus {

    private UdiOppholdsrettType eosEllerEFTABeslutningOmOppholdsrett;
    private LocalDate eosEllerEFTABeslutningOmOppholdsrettEffektuering;
    private UdiPeriode eosEllerEFTABeslutningOmOppholdsrettPeriode;

    private UdiOppholdstillatelse eosEllerEFTAOppholdstillatelse;
    private LocalDate eosEllerEFTAOppholdstillatelseEffektuering;
    private UdiPeriode eosEllerEFTAOppholdstillatelsePeriode;

    private UdiVarighetOpphold eosEllerEFTAVedtakOmVarigOppholdsrett;
    private LocalDate eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;
    private UdiPeriode eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;

    private UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;

    private UdiOppholdSammeVilkaar oppholdSammeVilkaar;

    private Boolean uavklart;
}
