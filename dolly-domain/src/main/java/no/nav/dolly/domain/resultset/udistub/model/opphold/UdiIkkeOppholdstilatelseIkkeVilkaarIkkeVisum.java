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
public class UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum {

    private UdiUtvistMedInnreiseForbud udiUtvistMedInnreiseForbud;
    private UdiAvslagEllerBortfall udiAvslagEllerBortfall;
    private String ovrigIkkeOppholdsKategoriArsak;
}
