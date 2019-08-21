package no.nav.registre.core.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.common.v2.Kodeverk;
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
import javax.persistence.Table;
import java.sql.Date;

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

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "kode", column = @Column(name = "utfallstype_kode_kode")),
                    @AttributeOverride(name = "visningsnavn", column = @Column(name = "utfallstype_kode_visningsnavn")),
                    @AttributeOverride(name = "type", column = @Column(name = "utfallstype_kode_type"))
            }
    )
    private Kodeverk utfallstypeKode;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "kode", column = @Column(name = "grunntype_kode_kode")),
                    @AttributeOverride(name = "visningsnavn", column = @Column(name = "grunntype_kode_visningsnavn")),
                    @AttributeOverride(name = "type", column = @Column(name = "grunntype_kode_type"))
            }
    )
    private Kodeverk grunntypeKode;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "kode", column = @Column(name = "tillatelse_kode_kode")),
                    @AttributeOverride(name = "visningsnavn", column = @Column(name = "tillatelse_kode_visningsnavn")),
                    @AttributeOverride(name = "type", column = @Column(name = "tillatelse_kode_type"))
            }
    )
    private Kodeverk tillatelseKode;

    private boolean erPositiv;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "kode", column = @Column(name = "utfall_kode")),
                    @AttributeOverride(name = "visningsnavn", column = @Column(name = "utfall_visningsnavn")),
                    @AttributeOverride(name = "type", column = @Column(name = "utfall_type"))
            }
    )
    Kodeverk utfallVarighetKode;

    Integer utfallVarighet;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "utfall_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "utfall_til"))
            }
    )
    Periode utfallPeriode;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "kode", column = @Column(name = "tillatelse_kode")),
                    @AttributeOverride(name = "visningsnavn", column = @Column(name = "tillatelse_visningsnavn")),
                    @AttributeOverride(name = "type", column = @Column(name = "tillatelse_type"))
            }
    )
    Kodeverk tillatelseVarighetKode;

    Integer tillatelseVarighet;

    @Embedded
    @AttributeOverrides(
            value = {
                    @AttributeOverride(name = "fra", column = @Column(name = "tillatelse_fra")),
                    @AttributeOverride(name = "til", column = @Column(name = "tillatelse_til"))
            }
    )
    Periode tillatelsePeriode;

    private Date effektueringsDato;
    private Date avgjoerelsesDato;
    private Date iverksettelseDato;
    private Date utreisefristDato;

	private String saksnummer;

    private String etat;

    private boolean flyktningstatus;
    private boolean uavklartFlyktningstatus;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Person person;
}
