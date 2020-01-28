package no.nav.registre.tp.database.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "T_K_MELDING_T")
@Getter
@Setter
@NoArgsConstructor
public class TKMeldingT {

    @Id
    private String kMeldingT;

    private String dekode;
    private Timestamp datoFom;
    private Timestamp datoTom;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String endretAv;
    private String erGyldig;

    @JsonCreator
    public TKMeldingT(
            @JsonProperty("kMeldingT") String kMeldingT,
            @JsonProperty("dekode") String dekode,
            @JsonProperty("datoFom") Timestamp datoFom,
            @JsonProperty("datoTom") Timestamp datoTom,
            @JsonProperty("datoOpprettet") Timestamp datoOpprettet,
            @JsonProperty("opprettetAv") String opprettetAv,
            @JsonProperty("datoEndret") Timestamp datoEndret,
            @JsonProperty("endretAv") String endretAv,
            @JsonProperty("erGyldig") String erGyldig
    ) {
        this.kMeldingT = kMeldingT;
        this.dekode = dekode;
        this.datoFom = datoFom != null ? new Timestamp(datoFom.getTime()) : null;
        this.datoTom = datoTom != null ? new Timestamp(datoTom.getTime()) : null;
        this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
        this.opprettetAv = opprettetAv;
        this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
        this.endretAv = endretAv;
        this.erGyldig = erGyldig;
    }

    public Timestamp getDatoFom() {
        return datoFom != null ? new Timestamp(datoFom.getTime()) : null;
    }

    public void setDatoFom(Timestamp datoFom) {
        this.datoFom = datoFom != null ? new Timestamp(datoFom.getTime()) : null;
    }

    public Timestamp getDatoTom() {
        return datoTom != null ? new Timestamp(datoTom.getTime()) : null;
    }

    public void setDatoTom(Timestamp datoTom) {
        this.datoTom = datoTom != null ? new Timestamp(datoTom.getTime()) : null;
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
