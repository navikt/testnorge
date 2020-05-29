package no.nav.registre.tp.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Timestamp;

@Entity(name = "T_K_YTELSE_T")
@Getter
@Setter
@NoArgsConstructor
public class TKYtelseT {

    @Id
    private String kYtelseT;
    private String dekode;
    private Date datoFom;
    private Date datoTom;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String endretAv;
    private String erGyldig;

    @SuppressWarnings("squid:S00107")
    public TKYtelseT(
            String kYtelseT,
            String dekode,
            Date datoFom,
            Date datoTom,
            Timestamp datoOpprettet,
            String opprettetAv,
            Timestamp datoEndret,
            String endretAv,
            String erGyldig
    ) {
        this.kYtelseT = kYtelseT;
        this.dekode = dekode;
        this.datoFom = datoFom != null ? new Date(datoFom.getTime()) : null;
        this.datoTom = datoTom != null ? new Date(datoTom.getTime()) : null;
        this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
        this.opprettetAv = opprettetAv;
        this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
        this.endretAv = endretAv;
        this.erGyldig = erGyldig;
    }

    public Date getDatoFom() {
        return datoFom != null ? new Date(datoFom.getTime()) : null;
    }

    public void setDatoFom(Date datoFom) {
        this.datoFom = datoFom != null ? new Date(datoFom.getTime()) : null;
    }

    public Date getDatoTom() {
        return datoTom != null ? new Date(datoTom.getTime()) : null;
    }

    public void setDatoTom(Date datoTom) {
        this.datoTom = datoTom != null ? new Date(datoTom.getTime()) : null;
    }

    public Timestamp getDatoOpprettet() {
        return datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
    }

    public void setDatoOpprettet(Timestamp datoOpprettet) {
        this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
    }

    public Timestamp getDatoEndret() {
        return datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
    }

    public void setDatoEndret(Timestamp datoEndret) {
        this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
    }
}
