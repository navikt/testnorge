package no.nav.registre.udistub.core.service.to.opphold;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.udistub.core.service.to.UdiPeriode;
import no.nav.registre.udistub.core.service.to.UdiPerson;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;

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
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eosEllerEFTABeslutningOmOppholdsrett;
    private UdiPeriode eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;
    private LocalDate eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eosEllerEFTAVedtakOmVarigOppholdsrett;
    private UdiPeriode eosEllerEFTAOppholdstillatelsePeriode;
    private LocalDate eosEllerEFTAOppholdstillatelseEffektuering;
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse eosEllerEFTAOppholdstillatelse;
    private UdiOppholdSammeVilkaar oppholdSammeVilkaar;
    private UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;
    @JsonBackReference
    private UdiPerson person;
}
