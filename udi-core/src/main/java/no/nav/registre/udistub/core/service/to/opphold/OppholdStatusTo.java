package no.nav.registre.udistub.core.service.to.opphold;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.udistub.core.service.to.PeriodeTo;
import no.nav.registre.udistub.core.service.to.PersonTo;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;

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
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eosEllerEFTABeslutningOmOppholdsrett;
    private PeriodeTo eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;
    private LocalDate eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eosEllerEFTAVedtakOmVarigOppholdsrett;
    private PeriodeTo eosEllerEFTAOppholdstillatelsePeriode;
    private LocalDate eosEllerEFTAOppholdstillatelseEffektuering;
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse eosEllerEFTAOppholdstillatelse;
    private OppholdSammeVilkaarTo oppholdSammeVilkaar;
    private IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;
    @JsonBackReference
    private PersonTo person;
}
