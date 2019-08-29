package no.nav.dolly.domain.resultset.udistub.model.opphold;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.PeriodeTo;
import no.nav.dolly.domain.resultset.udistub.model.PersonTo;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OppholdStatusTo {

    private Boolean uavklart;

    private PeriodeTo eosEllerEFTABeslutningOmOppholdsrettPeriode;
    private LocalDate eosEllerEFTABeslutningOmOppholdsrettEffektuering;
    private String eosEllerEFTABeslutningOmOppholdsrett;

    private PeriodeTo eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;
    private LocalDate eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;
    private String eosEllerEFTAVedtakOmVarigOppholdsrett;

    private PeriodeTo eosEllerEFTAOppholdstillatelsePeriode;
    private LocalDate eosEllerEFTAOppholdstillatelseEffektuering;
    private String eosEllerEFTAOppholdstillatelse;


    private OppholdSammeVilkaarTo oppholdSammeVilkaar;

    private IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;
    @JsonBackReference
    private PersonTo person;
}
