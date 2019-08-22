package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.Periode;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OppholdSammeVilkaar {
	private Periode oppholdSammeVilkaarPeriode;
	private Date oppholdSammeVilkaarEffektuering;
	private String oppholdstillatelseType;
	private Date oppholdstillatelseVedtaksDato;

}