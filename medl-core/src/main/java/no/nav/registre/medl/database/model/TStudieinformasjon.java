package no.nav.registre.medl.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "T_STUDIEINFORMASJON")
public class TStudieinformasjon {

    @Id
    @GeneratedValue(generator = "studieinformasjon_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "T_STUDIEINFORMASJON_SEQ", name = "studieinformasjon_gen", allocationSize = 1)
    @Column(name = "STUDIEINFORMASJON_ID")
    private Long studieinformasjonId;
    private String godkjent;
    private String delstudier;
    private String statsborgerland;
    private String studieland;
    private Date inndatoLaanekassen;

    public Date getInndatoLaanekassen() {
        if (inndatoLaanekassen != null) {
            return new Date(inndatoLaanekassen.getTime());
        } else {
            return null;
        }
    }

    public void setInndatoLaanekassen(Date inndatoLaanekassen) {
        if (inndatoLaanekassen != null) {
            this.inndatoLaanekassen = new Date(inndatoLaanekassen.getTime());
        } else {
            this.inndatoLaanekassen = null;
        }
    }
}
