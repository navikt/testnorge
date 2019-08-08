package no.nav.registre.tp;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
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

    public Ytelse() {

    }

    public Ytelse(Date datoInnmYtelFom, String kYtelseT, String meldingsType, Date datoYtelIverFom, Date datoYtelIverTom, Timestamp datoOpprettet, String opprettetAv, Timestamp datoEndret, String endretAv,
            String versjon, String erGyldig, Timestamp datoBrukFom, Timestamp datoBrukTom) {
        if (datoInnmYtelFom != null) {
            this.datoInnmYtelFom = new Date(datoInnmYtelFom.getTime());
        } else {
            this.datoInnmYtelFom = null;
        }

        this.kYtelseT = kYtelseT;
        this.meldingsType = meldingsType;

        if (datoYtelIverFom != null) {
            this.datoYtelIverFom = new Date(datoYtelIverFom.getTime());
        } else {
            this.datoYtelIverFom = null;
        }

        if (datoYtelIverTom != null) {
            this.datoYtelIverTom = new Date(datoYtelIverTom.getTime());
        } else {
            this.datoYtelIverTom = null;
        }

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

        this.endretAv = endretAv;
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

    public Date getDatoInnmYtelFom() {
        if (datoInnmYtelFom != null) {
            return new Date(datoInnmYtelFom.getTime());
        }
        return null;
    }

    public void setDatoInnmYtelFom(Date datoInnmYtelFom) {
        if (datoInnmYtelFom != null) {
            this.datoInnmYtelFom = new Date(datoInnmYtelFom.getTime());
        } else {
            this.datoInnmYtelFom = null;
        }
    }

    public Date getDatoYtelIverFom() {
        if (datoYtelIverFom != null) {
            return new Date(datoYtelIverFom.getTime());
        }
        return null;
    }

    public void setDatoYtelIverFom(Date datoYtelIverFom) {
        if (datoYtelIverFom != null) {
            this.datoYtelIverFom = new Date(datoYtelIverFom.getTime());
        } else {
            this.datoYtelIverFom = null;
        }
    }

    public Date getDatoYtelIverTom() {
        if (datoYtelIverTom != null) {
            return new Date(datoYtelIverTom.getTime());
        }
        return null;
    }

    public void setDatoYtelIverTom(Date datoYtelIverTom) {
        if (datoYtelIverTom != null) {
            this.datoYtelIverTom = new Date(datoYtelIverTom.getTime());
        } else {
            this.datoYtelIverTom = null;
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
            if (datoInnmYtelFom != null) {
                this.datoInnmYtelFom = new Date(datoInnmYtelFom.getTime());
            } else {
                this.datoInnmYtelFom = null;
            }
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
            if (datoYtelIverFom != null) {
                this.datoYtelIverFom = new Date(datoYtelIverFom.getTime());
            } else {
                this.datoYtelIverFom = null;
            }
            return this;
        }

        public Builder setDatoYtelIverTom(Date datoYtelIverTom) {
            if (datoYtelIverTom != null) {
                this.datoYtelIverTom = new Date(datoYtelIverTom.getTime());
            } else {
                this.datoYtelIverTom = null;
            }
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

        public Ytelse build() {
            return new Ytelse(datoInnmYtelFom, kYtelseT, meldingsType, datoYtelIverFom, datoYtelIverTom,
                    datoOpprettet, opprettetAv, datoEndret, endretAv, versjon, erGyldig, datoBrukFom, datoBrukTom);
        }
    }
}
