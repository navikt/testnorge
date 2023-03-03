package no.nav.udistub.database.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.OvrigIkkeOppholdsKategori;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class IkkeOppholdstilatelseIkkeVilkaarIkkeVisum {

    @Embedded
    private UtvistMedInnreiseForbud utvistMedInnreiseForbud;

    @Embedded
    private AvslagEllerBortfall avslagEllerBortfall;

    @AttributeOverride(name = "value", column = @Column(name = "overig_ikke_opphold_kategori_arsak"))
    private OvrigIkkeOppholdsKategori ovrigIkkeOppholdsKategoriArsak;
}
