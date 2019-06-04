package no.nav.registre.core.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "oppholds_statuser")
public class OppholdsStatus {

    @GeneratedValue
    @Id
    private Long id;

    private Boolean uavklart;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "value", column = @Column(name = "efta_beslutning_om_oppholdsrett_value"))
            }
    )
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eoSellerEFTABeslutningOmOppholdsrett;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_beslutning_om_oppholdsrett_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_beslutning_om_oppholdsrett_oppholds_til")),
                    @AttributeOverride(name = "effektuering", column = @Column(name = "efta_beslutning_om_oppholdsrett_oppholds_effektuering"))
            }
    )
    private Periode eoSellerEFTABeslutningOmOppholdsrettPeriode;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "value", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_value"))
            }
    )
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eoSellerEFTAVedtakOmVarigOppholdsrett;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_til")),
                    @AttributeOverride(name = "effektueringsdato", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_effektuering"))
            }
    )
    private Periode eoSellerEFTAVedtakOmVarigOppholdsrettPeriode;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "value", column = @Column(name = "efta_oppholdstillatels_value"))
            }
    )
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eoSellerEFTAOppholdstillatelse;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_oppholdstillatels_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_oppholdstillatels_til")),
                    @AttributeOverride(name = "effektueringsdato", column = @Column(name = "efta_oppholdstillatels_effektuering"))
            }
    )
    private Periode eoSellerEFTAOppholdstillatelsePeriode;

    private String oppholdSammeVilkaar;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "opphold_samme_vilkaar_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "opphold_samme_vilkaar_til")),
                    @AttributeOverride(name = "effektueringsdato", column = @Column(name = "opphold_samme_vilkaar_effektuering"))
            }
    )
    private Periode oppholdSammeVilkaarPeriode;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_fnr", nullable = false)
    @JsonBackReference
    private Person person;


}
