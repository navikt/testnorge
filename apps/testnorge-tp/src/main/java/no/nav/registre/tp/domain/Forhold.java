package no.nav.registre.tp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Forhold {

    private Date datoSamtykkeGitt;
    private String tssEksternIdFk;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String harUtlandPensj;
    private String endretAv;
    private String kilde;
    private String versjon;
    private String erGyldig;
    private Timestamp datoBrukFom;
    private Timestamp datoBrukTom;

    @SuppressWarnings("squid:S00107")
    public Forhold(
            Date datoSamtykkeGitt,
            String tssEksternIdFk,
            Timestamp datoOpprettet,
            String opprettetAv,
            Timestamp datoEndret,
            String harUtlandPensj,
            String endretAv,
            String kilde,
            String versjon,
            String erGyldig,
            Timestamp datoBrukFom,
            Timestamp datoBrukTom
    ) {
        this.datoSamtykkeGitt = datoSamtykkeGitt != null ? new Date(datoSamtykkeGitt.getTime()) : null;
        this.tssEksternIdFk = tssEksternIdFk;
        this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
        this.opprettetAv = opprettetAv;
        this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
        this.harUtlandPensj = harUtlandPensj;
        this.endretAv = endretAv;
        this.kilde = kilde;
        this.versjon = versjon;
        this.erGyldig = erGyldig;
        this.datoBrukFom = datoBrukFom != null ? new Timestamp(datoBrukFom.getTime()) : null;
        this.datoBrukTom = datoBrukTom != null ? new Timestamp(datoBrukTom.getTime()) : null;
    }

    public Date getDatoSamtykkeGitt() {
        return datoSamtykkeGitt != null ? new Timestamp(datoSamtykkeGitt.getTime()) : null;
    }

    public void setDatoSamtykkeGitt(Date datoSamtykkeGitt) {
        this.datoSamtykkeGitt = datoSamtykkeGitt != null ? new Date(datoSamtykkeGitt.getTime()) : null;
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

    public static class Builder {

        private Date datoSamtykkeGitt;
        private String tssEksternIdFk;
        private Timestamp datoOpprettet;
        private String opprettetAv;
        private Timestamp datoEndret;
        private String harUtlandPensj;
        private String endretAv;
        private String kilde;
        private String versjon;
        private String erGyldig;
        private Timestamp datoBrukFom;
        private Timestamp datoBrukTom;

        public Builder() {

        }

        public Builder setDatoSamtykkeGitt(Date datoSamtykkeGitt) {
            this.datoSamtykkeGitt = datoSamtykkeGitt != null ? new Date(datoSamtykkeGitt.getTime()) : null;
            return this;
        }

        public Builder setTssEksternIdFk(String tssEksternIdFk) {
            this.tssEksternIdFk = tssEksternIdFk;
            return this;
        }

        public Builder setDatoOpprettet(Timestamp datoOpprettet) {
            this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
            return this;
        }

        public Builder setOpprettetAv(String opprettetAv) {
            this.opprettetAv = opprettetAv;
            return this;
        }

        public Builder setDatoEndret(Timestamp datoEndret) {
            this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
            return this;
        }

        public Builder setHarUtlandPensj(String harUtlandPensj) {
            this.harUtlandPensj = harUtlandPensj;
            return this;
        }

        public Builder setEndretAv(String endretAv) {
            this.endretAv = endretAv;
            return this;
        }

        public Builder setKilde(String kilde) {
            this.kilde = kilde;
            return this;
        }

        public Builder setVersjon(String versjon) {
            this.versjon = versjon;
            return this;
        }

        public Builder setErGyldig(String erGyldig) {
            this.erGyldig = erGyldig;
            return this;
        }

        public Builder setDatoBrukFom(Timestamp datoBrukFom) {
            this.datoBrukFom = datoBrukFom != null ? new Timestamp(datoBrukFom.getTime()) : null;
            return this;
        }

        public Builder setDatoBrukTom(Timestamp datoBrukTom) {
            this.datoBrukTom = datoBrukTom != null ? new Timestamp(datoBrukTom.getTime()) : null;
            return this;
        }

        public Forhold build() {
            return new Forhold(datoSamtykkeGitt, tssEksternIdFk, datoOpprettet, opprettetAv, datoEndret, harUtlandPensj, endretAv, kilde, versjon, erGyldig, datoBrukFom, datoBrukTom);
        }
    }
}
