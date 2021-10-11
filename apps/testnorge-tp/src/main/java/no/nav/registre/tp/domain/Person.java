package no.nav.registre.tp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class Person {

    private String fnrFk;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String endretAv;
    private String versjon;

    public Person(
            String fnrFk,
            Timestamp datoOpprettet,
            String opprettetAv,
            Timestamp datoEndret,
            String endretAv,
            String versjon
    ) {
        this.fnrFk = fnrFk;
        this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
        this.opprettetAv = opprettetAv;
        this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
        this.endretAv = endretAv;
        this.versjon = versjon;
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

        public Person build() {
            return new Person(fnrFk, datoOpprettet, opprettetAv, datoEndret, endretAv, versjon);
        }
    }
}
