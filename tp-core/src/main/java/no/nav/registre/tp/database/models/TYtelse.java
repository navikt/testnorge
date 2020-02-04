package no.nav.registre.tp.database.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.sql.Date;
import java.sql.Timestamp;

@Entity(name = "T_YTELSE")
@Getter
@Setter
@NoArgsConstructor
public class TYtelse {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ytelse_seq")
    @SequenceGenerator(sequenceName = "T_YTELSE_SEQ", name = "ytelse_seq", allocationSize = 1)

    @JsonIgnore
    @Id
    private Integer ytelseId;

    @JsonProperty("dato_innm_ytel_fom")
    private Date datoInnmYtelFom;

    @JsonProperty("k_ytelse_t")
    @Column(name = "K_YTELSE_T")
    private String kYtelseT;

    @JsonProperty("k_melding_t")
    @Column(name = "K_MELDING_T")
    private String kMeldingT;

    @JsonProperty("dato_ytel_iver_fom")
    private Date datoYtelIverFom;
    @JsonProperty("dato_ytel_iver_tom")
    private Date datoYtelIverTom;
    @JsonProperty("dato_opprettet")
    private Timestamp datoOpprettet;
    @JsonProperty("opprettet_av")
    private String opprettetAv;
    @JsonProperty("dato_endret")
    private Timestamp datoEndret;
    @JsonProperty("endret_av")
    private String endretAv;
    @JsonProperty("versjon")
    private String versjon;
    @JsonProperty("er_gyldig")
    private String erGyldig;

    @JsonProperty("funk_ytelse_id")
    private String funkYtelseId;
    @JsonProperty("dato_bruk_fom")
    private Timestamp datoBrukFom;
    @JsonProperty("dato_bruk_tom")
    private Timestamp datoBrukTom;

    @SuppressWarnings("squid:S00107")
    public TYtelse(
            Date datoInnmYtelFom,
            String kYtelseT,
            String kMeldingT,
            Date datoYtelIverFom,
            Date datoYtelIverTom,
            Timestamp datoOpprettet,
            String opprettetAv,
            Timestamp datoEndret,
            String endretAv,
            String versjon,
            String erGyldig,
            String funkYtelseId,
            Timestamp datoBrukFom,
            Timestamp datoBrukTom
    ) {
        this.datoInnmYtelFom = datoInnmYtelFom != null ? new Date(datoInnmYtelFom.getTime()) : null;
        this.kYtelseT = kYtelseT;
        this.kMeldingT = kMeldingT;
        this.datoYtelIverFom = datoYtelIverFom != null ? new Date(datoYtelIverFom.getTime()) : null;
        this.datoYtelIverTom = datoYtelIverTom != null ? new Date(datoYtelIverTom.getTime()) : null;
        this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
        this.opprettetAv = opprettetAv;
        this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
        this.endretAv = endretAv;
        this.versjon = versjon;
        this.erGyldig = erGyldig;
        this.funkYtelseId = funkYtelseId;
        this.datoBrukFom = datoBrukFom != null ? new Timestamp(datoBrukFom.getTime()) : null;
        this.datoBrukTom = datoBrukTom != null ? new Timestamp(datoBrukTom.getTime()) : null;
    }

    public Date getDatoInnmYtelFom() {
        return datoInnmYtelFom != null ? new Date(datoInnmYtelFom.getTime()) : null;
    }

    public void setDatoInnmYtelFom(Date datoInnmYtelFom) {
        this.datoInnmYtelFom = datoInnmYtelFom != null ? new Date(datoInnmYtelFom.getTime()) : null;
    }

    public Date getDatoYtelIverFom() {
        return datoYtelIverFom != null ? new Date(datoYtelIverFom.getTime()) : null;
    }

    public void setDatoYtelIverFom(Date datoYtelIverFom) {
        this.datoYtelIverFom = datoYtelIverFom != null ? new Date(datoYtelIverFom.getTime()) : null;
    }

    public Date getDatoYtelIverTom() {
        return datoYtelIverTom != null ? new Date(datoYtelIverTom.getTime()) : null;
    }

    public void setDatoYtelIverTom(Date datoYtelIverTom) {
        this.datoYtelIverTom = datoYtelIverTom != null ? new Date(datoYtelIverTom.getTime()) : null;
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

    public Timestamp getDatoBrukFom() {
        return datoBrukFom != null ? new Timestamp(datoBrukFom.getTime()) : null;
    }

    public void setDatoBrukFom(Timestamp datoBrukFom) {
        this.datoBrukFom = datoBrukFom != null ? new Timestamp(datoBrukFom.getTime()) : null;
    }

    public Timestamp getDatoBrukTom() {
        return datoBrukTom != null ? new Timestamp(datoBrukTom.getTime()) : null;
    }

    public void setDatoBrukTom(Timestamp datoBrukTom) {
        this.datoBrukTom = datoBrukTom != null ? new Timestamp(datoBrukTom.getTime()) : null;
    }
}
