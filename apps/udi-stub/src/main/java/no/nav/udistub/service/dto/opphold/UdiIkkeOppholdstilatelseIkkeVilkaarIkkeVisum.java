package no.nav.udistub.service.dto.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.OvrigIkkeOppholdsKategori;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum {

    private UdiUtvistMedInnreiseForbud utvistMedInnreiseForbud;
    private UdiAvslagEllerBortfall avslagEllerBortfall;
    private OvrigIkkeOppholdsKategori ovrigIkkeOppholdsKategoriArsak;
}
