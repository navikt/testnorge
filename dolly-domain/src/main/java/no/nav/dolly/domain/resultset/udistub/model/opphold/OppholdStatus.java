package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class OppholdStatus {

	private Boolean uavklart;

	private Periode eosEllerEFTABeslutningOmOppholdsrettPeriode;
	private Date eosEllerEFTABeslutningOmOppholdsrettEffektuering;
	private String eosEllerEFTABeslutningOmOppholdsrett;

	private Periode eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;
	private Date eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;
	private String eosEllerEFTAVedtakOmVarigOppholdsrett;

	private Periode eosEllerEFTAOppholdstillatelsePeriode;
	private Date eosEllerEFTAOppholdstillatelseEffektuering;
	private String eosEllerEFTAOppholdstillatelse;

	private OppholdSammeVilkaar oppholdSammeVilkaar;
	private IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;
	private Person person;
}
