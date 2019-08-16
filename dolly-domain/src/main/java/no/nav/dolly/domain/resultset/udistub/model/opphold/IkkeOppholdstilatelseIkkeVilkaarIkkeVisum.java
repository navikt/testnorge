package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class IkkeOppholdstilatelseIkkeVilkaarIkkeVisum {

	private UtvistMedInnreiseForbud utvistMedInnreiseForbud;
	private AvslagEllerBortFall avslagEllerBortFall;
	private String ovrigIkkeOppholdsKategoriArsak;

}
