package no.nav.registre.tp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Ytelse {

    private Date datoInnmYtelFom;
    private String kYtelseT;
    private String meldingsType;
    private Date datoYtelIverFom;
    private Date datoYtelIverTom;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String endretAv;
    private String versjon;
    private String erGyldig;
    private Timestamp datoBrukFom;
    private Timestamp datoBrukTom;

    @SuppressWarnings("squid:S00107")
    public Ytelse(
            Date datoInnmYtelFom,
            String kYtelseT,
            String meldingsType,
            Date datoYtelIverFom,
            Date datoYtelIverTom,
            Timestamp datoOpprettet,
            String opprettetAv,
            Timestamp datoEndret,
            String endretAv,
            String versjon,
            String erGyldig,
            Timestamp datoBrukFom,
            Timestamp datoBrukTom
    ) {
        this.datoInnmYtelFom = datoInnmYtelFom != null ? new Date(datoInnmYtelFom.getTime()) : null;
        this.kYtelseT = kYtelseT;
        this.meldingsType = meldingsType;
        this.datoYtelIverFom = datoYtelIverFom != null ? new Date(datoYtelIverFom.getTime()) : null;
        this.datoYtelIverTom = datoYtelIverTom != null ? new Date(datoYtelIverTom.getTime()) : null;
        this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
        this.opprettetAv = opprettetAv;
        this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
        this.endretAv = endretAv;
        this.versjon = versjon;
        this.erGyldig = erGyldig;
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

    public static class Builder {

        private Date datoInnmYtelFom;
        private String kYtelseT;
        private String meldingsType;
        private Date datoYtelIverFom;
        private Date datoYtelIverTom;
        private Timestamp datoOpprettet;
        private String opprettetAv;
        private Timestamp datoEndret;
        private String endretAv;
        private String versjon;
        private String erGyldig;
        private Timestamp datoBrukFom;
        private Timestamp datoBrukTom;

        public Builder() {

        }

        public Builder setDatoInnmYtelFom(Date datoInnmYtelFom) {
            this.datoInnmYtelFom = datoInnmYtelFom != null ? new Date(datoInnmYtelFom.getTime()) : null;
            return this;
        }

        public Builder setKYtelseT(String kYtelseT) {
            this.kYtelseT = kYtelseT;
            return this;
        }

        public Builder setMeldingsType(String meldingsType) {
            this.meldingsType = meldingsType;
            return this;
        }

        public Builder setDatoYtelIverFom(Date datoYtelIverFom) {
            this.datoYtelIverFom = datoYtelIverFom != null ? new Date(datoYtelIverFom.getTime()) : null;
            return this;
        }

        public Builder setDatoYtelIverTom(Date datoYtelIverTom) {
            this.datoYtelIverTom = datoYtelIverTom != null ? new Date(datoYtelIverTom.getTime()) : null;
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

        public Builder setEndretAv(String endretAv) {
            this.endretAv = endretAv;
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

        public Ytelse build() {
            return new Ytelse(datoInnmYtelFom, kYtelseT, meldingsType, datoYtelIverFom, datoYtelIverTom,
                    datoOpprettet, opprettetAv, datoEndret, endretAv, versjon, erGyldig, datoBrukFom, datoBrukTom);
        }
    }
}
