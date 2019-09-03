package no.nav.registre.udistub.core.service.to.opphold;

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
public class IkkeOppholdstilatelseIkkeVilkaarIkkeVisumTo {

    private UtvistMedInnreiseForbudTo utvistMedInnreiseForbud;
    private AvslagEllerBortfallTo avslagEllerBortfall;
    private OvrigIkkeOppholdsKategori ovrigIkkeOppholdsKategoriArsak;
}
