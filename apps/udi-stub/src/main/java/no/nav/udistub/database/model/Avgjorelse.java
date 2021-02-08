package no.nav.udistub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "avgjoerelse")
public class Avgjorelse {

    @GeneratedValue
    @Id
    private Long id;

    private String omgjortAvgjoerelsesId;

    @OneToOne
    @JoinColumn(name = "utfallstype_kode_id")
    private Kodeverk utfallstypeKode;

    @OneToOne
    @JoinColumn(name = "grunntype_kode_id")
    private Kodeverk grunntypeKode;

    @OneToOne
    @JoinColumn(name = "tillatelse_kode_id")
    private Kodeverk tillatelseKode;

    @OneToOne
    @JoinColumn(name = "utfall_varighet_kode_id")
    private Kodeverk utfallVarighetKode;

    @OneToOne
    @JoinColumn(name = "tillatelse_varighet_kode_id")
    private Kodeverk tillatelseVarighetKode;

    private boolean erPositiv;

    private Integer utfallVarighet;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "utfall_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "utfall_til"))
            }
    )
    private Periode utfallPeriode;

    private Integer tillatelseVarighet;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "tillatelse_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "tillatelse_til"))
            }
    )
    private Periode tillatelsePeriode;

    private LocalDate effektueringsDato;
    private LocalDate avgjoerelsesDato;
    private LocalDate iverksettelseDato;
    private LocalDate utreisefristDato;

    private String saksnummer;

    private String etat;

    private boolean harFlyktningstatus;
    private boolean uavklartFlyktningstatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person;
}
