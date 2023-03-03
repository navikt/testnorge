package no.nav.udistub.database.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.udistub.database.model.Periode;
import no.udi.mt_1067_nav_data.v1.OppholdstillatelseKategori;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OppholdSammeVilkaar {

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "opphold_samme_vilkaar_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "opphold_samme_vilkaar_til")),
            }
    )
    private Periode oppholdSammeVilkaarPeriode;
    private LocalDate oppholdSammeVilkaarEffektuering;

    @AttributeOverride(name = "value", column = @Column(name = "oppholdstillatelse_value"))
    private OppholdstillatelseKategori oppholdstillatelseType;

    private LocalDate oppholdstillatelseVedtaksDato;
}