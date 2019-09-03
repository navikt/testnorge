package no.nav.dolly.domain.resultset.udistub.model.opphold;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.UdiPeriode;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UdiOppholdStatus {

    private Boolean uavklart;

    private UdiPeriode eosEllerEFTABeslutningOmOppholdsrettPeriode;
    private LocalDate eosEllerEFTABeslutningOmOppholdsrettEffektuering;
    private String eosEllerEFTABeslutningOmOppholdsrett;

    private UdiPeriode eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;
    private LocalDate eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;
    private String eosEllerEFTAVedtakOmVarigOppholdsrett;

    private UdiPeriode eosEllerEFTAOppholdstillatelsePeriode;
    private LocalDate eosEllerEFTAOppholdstillatelseEffektuering;
    private String eosEllerEFTAOppholdstillatelse;


    private UdiOppholdSammeVilkaar udiOppholdSammeVilkaar;

    private UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum udiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
    @JsonBackReference
    private UdiPerson person;
}
