package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class IkkeOppholdstilatelseIkkeVilkaarIkkeVisum {

    private UtvistMedInnreiseForbud utvistMedInnreiseForbud;
    private AvslagEllerBortfall avslagEllerBortfall;
    private String ovrigIkkeOppholdsKategoriArsak;
}
