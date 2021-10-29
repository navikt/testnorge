package no.nav.dolly.domain.resultset.udistub.model.opphold;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPeriode;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsUdiOppholdStatus {

    private UdiOppholdsrettType eosEllerEFTABeslutningOmOppholdsrett;
    private LocalDateTime eosEllerEFTABeslutningOmOppholdsrettEffektuering;
    private RsUdiPeriode eosEllerEFTABeslutningOmOppholdsrettPeriode;

    private UdiOppholdstillatelse eosEllerEFTAOppholdstillatelse;
    private LocalDateTime eosEllerEFTAOppholdstillatelseEffektuering;
    private RsUdiPeriode eosEllerEFTAOppholdstillatelsePeriode;

    private UdiVarighetOpphold eosEllerEFTAVedtakOmVarigOppholdsrett;
    private LocalDateTime eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;
    private RsUdiPeriode eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;

    private RsUdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;

    private RsUdiOppholdSammeVilkaar oppholdSammeVilkaar;

    private Boolean uavklart;
}
