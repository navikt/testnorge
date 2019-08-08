package no.nav.registre.core.database.model.opphold;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.core.database.model.Periode;
import no.nav.registre.core.database.model.Person;
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
@Setter
@Getter
@Table(name = "opphold_status")
public class OppholdStatus {

    @Id
    @GeneratedValue
    private Long id;

    private Boolean uavklart;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_beslutning_om_oppholdsrett_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_beslutning_om_oppholdsrett_oppholds_til"))
            }
    )
    private Periode eoSellerEFTABeslutningOmOppholdsrettPeriode;
    private Date eoSellerEFTABeslutningOmOppholdsrettEffektuering;

    @AttributeOverride(name = "value", column = @Column(name = "efta_beslutning_om_oppholdsrett_value"))
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eoSellerEFTABeslutningOmOppholdsrett;


    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_til"))
            }
    )
    private Periode eoSellerEFTAVedtakOmVarigOppholdsrettPeriode;
    private Date eoSellerEFTAVedtakOmVarigOppholdsrettEffektuering;

    @AttributeOverride(name = "value", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_value"))
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eoSellerEFTAVedtakOmVarigOppholdsrett;


    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_oppholdstillatelse_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_oppholdstillatelse_til"))
            }
    )
    private Periode eoSellerEFTAOppholdstillatelsePeriode;
    private Date eoSellerEFTAOppholdstillatelseEffektuering;
    @AttributeOverride(name = "value", column = @Column(name = "efta_oppholdstillatelse_value"))
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse eoSellerEFTAOppholdstillatelse;

    @Embedded
    private OppholdSammeVilkaar oppholdSammeVilkaar;

    @Embedded
    private IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_fnr", nullable = false)
    @JsonBackReference
    private Person person;
}
