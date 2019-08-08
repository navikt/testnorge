package no.nav.registre.tp;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
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

    public Forhold() {

    }

    public Forhold(Date datoSamtykkeGitt, String tssEksternIdFk, Timestamp datoOpprettet, String opprettetAv, Timestamp datoEndret, String harUtlandPensj, String endretAv, String kilde, String versjon,
            String erGyldig, Timestamp datoBrukFom, Timestamp datoBrukTom) {
        if (datoSamtykkeGitt != null) {
            this.datoSamtykkeGitt = new Date(datoSamtykkeGitt.getTime());
        } else {
            this.datoSamtykkeGitt = null;
        }

        this.tssEksternIdFk = tssEksternIdFk;

        if (datoOpprettet != null) {
            this.datoOpprettet = new Timestamp(datoOpprettet.getTime());
        } else {
            this.datoOpprettet = null;
        }

        this.opprettetAv = opprettetAv;

        if (datoEndret != null) {
            this.datoEndret = new Timestamp(datoEndret.getTime());
        } else {
            this.datoEndret = null;
        }

        this.harUtlandPensj = harUtlandPensj;
        this.endretAv = endretAv;
        this.kilde = kilde;
        this.versjon = versjon;
        this.erGyldig = erGyldig;

        if (datoBrukFom != null) {
            this.datoBrukFom = new Timestamp(datoBrukFom.getTime());
        } else {
            this.datoBrukFom = null;
        }

        if (datoBrukTom != null) {
            this.datoBrukTom = new Timestamp(datoBrukTom.getTime());
        } else {
            this.datoBrukTom = null;
        }
    }

    public Date getDatoSamtykkeGitt() {
        if (datoSamtykkeGitt != null) {
            return new Date(datoSamtykkeGitt.getTime());
        }
        return null;
    }

    public void setDatoSamtykkeGitt(Date datoSamtykkeGitt) {
        if (datoSamtykkeGitt != null) {
            this.datoSamtykkeGitt = new Date(datoSamtykkeGitt.getTime());
        } else {
            this.datoSamtykkeGitt = null;
        }
    }

    public Timestamp getDatoOpprettet() {
        if (datoOpprettet != null) {
            return new Timestamp(datoOpprettet.getTime());
        }
        return null;
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
        }
        return null;
    }

    public void setDatoEndret(Timestamp datoEndret) {
        if (datoEndret != null) {
            this.datoEndret = new Timestamp(datoEndret.getTime());
        } else {
            this.datoEndret = null;
        }
    }

    public Timestamp getDatoBrukFom() {
        if (datoBrukFom != null) {
            return new Timestamp(datoBrukFom.getTime());
        }
        return null;
    }

    public void setDatoBrukFom(Timestamp datoBrukFom) {
        if (datoBrukFom != null) {
            this.datoBrukFom = new Timestamp(datoBrukFom.getTime());
        } else {
            this.datoBrukFom = null;
        }
    }

    public Timestamp getDatoBrukTom() {
        if (datoBrukTom != null) {
            return new Timestamp(datoBrukTom.getTime());
        }
        return null;
    }

    public void setDatoBrukTom(Timestamp datoBrukTom) {
        if (datoBrukTom != null) {
            this.datoBrukTom = new Timestamp(datoBrukTom.getTime());
        } else {
            this.datoBrukTom = null;
        }
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
            if (datoSamtykkeGitt != null) {
                this.datoSamtykkeGitt = new Date(datoSamtykkeGitt.getTime());
            } else {
                this.datoSamtykkeGitt = null;
            }
            return this;
        }

        public Builder setTssEksternIdFk(String tssEksternIdFk) {
            this.tssEksternIdFk = tssEksternIdFk;
            return this;
        }

        public Builder setDatoOpprettet(Timestamp datoOpprettet) {
            if (datoOpprettet != null) {
                this.datoOpprettet = new Timestamp(datoOpprettet.getTime());
            } else {
                this.datoOpprettet = null;
            }
            return this;
        }

        public Builder setOpprettetAv(String opprettetAv) {
            this.opprettetAv = opprettetAv;
            return this;
        }

        public Builder setDatoEndret(Timestamp datoEndret) {
            if (datoEndret != null) {
                this.datoEndret = new Timestamp(datoEndret.getTime());
            } else {
                this.datoEndret = null;
            }
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
            if (datoBrukFom != null) {
                this.datoBrukFom = new Timestamp(datoBrukFom.getTime());
            } else {
                this.datoBrukFom = null;
            }
            return this;
        }

        public Builder setDatoBrukTom(Timestamp datoBrukTom) {
            if (datoBrukTom != null) {
                this.datoBrukTom = new Timestamp(datoBrukTom.getTime());
            } else {
                this.datoBrukTom = null;
            }
            return this;
        }

        public Forhold build() {
            return new Forhold(datoSamtykkeGitt, tssEksternIdFk, datoOpprettet, opprettetAv, datoEndret, harUtlandPensj, endretAv, kilde, versjon, erGyldig, datoBrukFom, datoBrukTom);
        }
    }
}
