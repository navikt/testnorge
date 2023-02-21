package no.nav.udistub.database.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.udistub.database.model.Periode;
import no.nav.udistub.database.model.Person;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

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
    private Periode eosEllerEFTABeslutningOmOppholdsrettPeriode;
    private LocalDate eosEllerEFTABeslutningOmOppholdsrettEffektuering;

    @AttributeOverride(name = "value", column = @Column(name = "efta_beslutning_om_oppholdsrett_value"))
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eosEllerEFTABeslutningOmOppholdsrett;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_til"))
            }
    )
    private Periode eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;
    private LocalDate eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;

    @AttributeOverride(name = "value", column = @Column(name = "efta_vedtak_om_varig_oppholdsrett_value"))
    private EOSellerEFTAGrunnlagskategoriOppholdsrett eosEllerEFTAVedtakOmVarigOppholdsrett;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "efta_oppholdstillatelse_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "efta_oppholdstillatelse_til"))
            }
    )
    private Periode eosEllerEFTAOppholdstillatelsePeriode;
    private LocalDate eosEllerEFTAOppholdstillatelseEffektuering;
    @AttributeOverride(name = "value", column = @Column(name = "efta_oppholdstillatelse_value"))
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse eosEllerEFTAOppholdstillatelse;

    @Embedded
    private OppholdSammeVilkaar oppholdSammeVilkaar;

    @Embedded
    private IkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}
