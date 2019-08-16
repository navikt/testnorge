package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.Periode;
import no.nav.dolly.domain.resultset.udistub.model.Person;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OppholdStatus {

	private Boolean uavklart;
	private Periode eoSellerEFTABeslutningOmOppholdsrettPeriode;

	private Date eoSellerEFTABeslutningOmOppholdsrettEffektuering;
	private String eoSellerEFTABeslutningOmOppholdsrett;
	private Periode eoSellerEFTAVedtakOmVarigOppholdsrettPeriode;
	private Date eoSellerEFTAVedtakOmVarigOppholdsrettEffektuering;
	private String eoSellerEFTAVedtakOmVarigOppholdsrett;

	private Periode eoSellerEFTAOppholdstillatelsePeriode;
	private Date eoSellerEFTAOppholdstillatelseEffektuering;
	private String eoSellerEFTAOppholdstillatelse;

	private OppholdSammeVilkaar oppholdSammeVilkaar;
	private IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;
	private Person person;
}
