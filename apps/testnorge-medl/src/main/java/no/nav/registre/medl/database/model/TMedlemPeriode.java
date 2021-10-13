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
@EqualsAndHashCode
@Entity
@Table(name = "T_MEDLEM_PERIODE")
public class TMedlemPeriode {

    @Id
    @GeneratedValue(generator = "medlem_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "T_MEDLEM_PERIODE_SEQ", name = "medlem_gen", allocationSize = 1)
    @Column(name = "MEDLEM_PERIODE_ID")
    private Long medlemPeriodeId;
    private Date periodeFom;
    private Date periodeTom;
    @NonNull
    private String type;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String endretAv;
    private Date datoIdentifisering;
    @NonNull
    @Column(name = "AKTOER_ID")
    private Long aktoerId;
    @NonNull
    private String versjon;
    private String kilde;
    private String status;
    private String dekning;
    private Date datoBeslutning;
    private String lovvalg;
    private String statusaarsak;
    private Date datoRegistrert;
    private String kildedokument;
    private Long funkPeriodeId;
    private Timestamp datoBrukFra;
    private Timestamp datoBrukTil;
    @NonNull
    private Long studieinformasjonId;
    private String grunnlag;
    private String land;

    public Date getPeriodeFom() {
        if (periodeFom != null) {
            return new Date(periodeFom.getTime());
        } else {
            return null;
        }
    }

    public void setPeriodeFom(Date periodeFom) {
        if (periodeFom != null) {
            this.periodeFom = new Date(periodeFom.getTime());
        } else {
            this.periodeFom = null;
        }
    }

    public Date getPeriodeTom() {
        if (periodeTom != null) {
            return new Date(periodeTom.getTime());
        } else {
            return null;
        }
    }

    public void setPeriodeTom(Date periodeTom) {
        if (periodeTom != null) {
            this.periodeTom = new Date(periodeTom.getTime());
        } else {
            this.periodeTom = null;
        }
    }

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

    public Date getDatoIdentifisering() {
        if (datoIdentifisering != null) {
            return new Date(datoIdentifisering.getTime());
        } else {
            return null;
        }
    }

    public void setDatoIdentifisering(Date datoIdentifisering) {
        if (datoIdentifisering != null) {
            this.datoIdentifisering = new Date(datoIdentifisering.getTime());
        } else {
            this.datoIdentifisering = null;
        }
    }

    public Date getDatoBeslutning() {
        if (datoBeslutning != null) {
            return new Date(datoBeslutning.getTime());
        } else {
            return null;
        }
    }

    public void setDatoBeslutning(Date datoBeslutning) {
        if (datoBeslutning != null) {
            this.datoBeslutning = new Date(datoBeslutning.getTime());
        } else {
            this.datoBeslutning = null;
        }
    }

    public Date getDatoRegistrert() {
        if (datoRegistrert != null) {
            return new Date(datoRegistrert.getTime());
        } else {
            return null;
        }
    }

    public void setDatoRegistrert(Date datoRegistrert) {
        if (datoRegistrert != null) {
            this.datoRegistrert = new Date(datoRegistrert.getTime());
        } else {
            this.datoRegistrert = null;
        }
    }

    public Timestamp getDatoBrukFra() {
        if (datoBrukFra != null) {
            return new Timestamp(datoBrukFra.getTime());
        } else {
            return null;
        }
    }

    public void setDatoBrukFra(Timestamp datoBrukFra) {
        if (datoBrukFra != null) {
            this.datoBrukFra = new Timestamp(datoBrukFra.getTime());
        } else {
            this.datoBrukFra = null;
        }
    }

    public Timestamp getDatoBrukTil() {
        if (datoBrukTil != null) {
            return new Timestamp(datoBrukTil.getTime());
        } else {
            return null;
        }
    }

    public void setDatoBrukTil(Timestamp datoBrukTil) {
        if (datoBrukTil != null) {
            this.datoBrukTil = new Timestamp(datoBrukTil.getTime());
        } else {
            this.datoBrukTil = null;
        }
    }
}
