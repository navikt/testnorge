package no.nav.registre.tp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class Person {

    private String fnrFk;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String endretAv;
    private String versjon;

    public Person() {

    }

    public Person(String fnrFk, Timestamp datoOpprettet, String opprettetAv, Timestamp datoEndret, String endretAv, String versjon) {
        this.fnrFk = fnrFk;

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

    public static class Builder {

        private String fnrFk;
        private Timestamp datoOpprettet;
        private String opprettetAv;
        private Timestamp datoEndret;
        private String endretAv;
        private String versjon;

        public Builder() {

        }

        public Builder setFnrFk(String fnrFk) {
            this.fnrFk = fnrFk;
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

        public Person build() {
            return new Person(fnrFk, datoOpprettet, opprettetAv, datoEndret, endretAv, versjon);
        }
    }
}
