package no.nav.registre.core.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;

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
import java.sql.Date;

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
                    @AttributeOverride(name = "til", column = @Column(name = "efta_beslutning_om_oppholdsrett_oppholds_til"))
            }
    )
    private Periode eoSellerEFTABeslutningOmOppholdsrettPeriode;
    private Date eoSellerEFTABeslutningOmOppholdsrettEffektuering;

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
                    @AttributeOverride(name = "til", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_til"))
            }
    )
    private Periode eoSellerEFTAVedtakOmVarigOppholdsrettPeriode;
    private Date eoSellerEFTAVedtakOmVarigOppholdsrettEffektuering;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "value", column = @Column(name = "efta_oppholdstillatels_value"))
            }
    )
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse eoSellerEFTAOppholdstillatelse;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_oppholdstillatels_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_oppholdstillatels_til"))
            }
    )
    private Periode eoSellerEFTAOppholdstillatelsePeriode;
    private Date eoSellerEFTAOppholdstillatelseEffektuering;

    private String oppholdSammeVilkaar;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "opphold_samme_vilkaar_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "opphold_samme_vilkaar_til")),
            }
    )
    private Periode oppholdSammeVilkaarPeriode;
    private Date oppholdSammeVilkaarEffektuering;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_fnr", nullable = false)
    @JsonBackReference
    private Person person;


}
