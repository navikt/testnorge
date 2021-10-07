package no.nav.registre.medl.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode
@Table(name = "T_AKTOER")
public class TAktoer {

    @Id
    @GeneratedValue(generator = "aktoer_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "T_AKTOER_SEQ", name = "aktoer_gen", allocationSize = 1)
    @Column(name = "AKTOER_ID")
    private Long id;
    private Timestamp datoOpprettet;
    private Timestamp datoEndret;
    @NonNull
    private String ident;
    @Column(name = "AKTOERID")
    private String aktoerid;
    private Date sistSynkronisert;

    public Timestamp getDatoOpprettet() {
        if (datoOpprettet != null) {
            return new Timestamp(datoOpprettet.getTime());
        } else {
            return null;
        }
    }

    public void setDatoOpprettet(Timestamp datoOpprettet) {
        if (datoOpprettet != null) {
            this.datoOpprettet = new Timestamp(datoOpprettet.getTime());
        } else {
            this.datoOpprettet = null;
        }
    }

    public Timestamp getDatoEndret() {
        if (datoEndret != null) {
            return new Timestamp(datoEndret.getTime());
        } else {
            return null;
        }
    }

    public void setDatoEndret(Timestamp datoEndret) {
        if (datoEndret != null) {
            this.datoEndret = new Timestamp(datoEndret.getTime());
        } else {
            this.datoEndret = null;
        }
    }

    public Date getSistSynkronisert() {
        if (sistSynkronisert != null) {
            return new Date(sistSynkronisert.getTime());
        } else {
            return null;
        }
    }

    public void setSistSynkronisert(Date sistSynkronisert) {
        if (sistSynkronisert != null) {
            this.sistSynkronisert = new Date(sistSynkronisert.getTime());
        } else {
            this.sistSynkronisert = null;
        }
    }
}
