package no.nav.registre.tp.database.models;

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

@Entity(name = "T_FORHOLD")
@Getter
@Setter
@NoArgsConstructor
public class TForhold {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forhold_seq")
    @SequenceGenerator(sequenceName = "T_FORHOLD_SEQ", name = "forhold_seq", allocationSize = 1)

    @Id
    private Integer forholdId;
    private Integer personId;
    private Date datoSamtykkeGitt;
    private String tssEksternIdFk;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String harUtlandPensj;
    private String endretAv;
    @Column(name = "K_KILDE_TP_T")
    private String kKildeTpT;
    private String versjon;
    private String erGyldig;
    @Column(name = "K_SAMTYKKE_SIM_T")
    private String kSamtykkeSimT;
    private String harSimulering;
    private String funkForholdId;
    private Timestamp datoBrukFom;
    private Timestamp datoBrukTom;

    @SuppressWarnings("squid:S00107")
    public TForhold(
            Integer personId,
            Date datoSamtykkeGitt,
            String tssEksternIdFk,
            Timestamp datoOpprettet,
            String opprettetAv,
            Timestamp datoEndret,
            String harUtlandPensj,
            String endretAv,
            String kKildeTpT,
            String versjon,
            String erGyldig,
            String kSamtykkeSimT,
            String harSimulering,
            String funkForholdId,
            Timestamp datoBrukFom,
            Timestamp datoBrukTom
    ) {
        this.personId = personId;
        this.datoSamtykkeGitt = datoSamtykkeGitt != null ? new Date(datoSamtykkeGitt.getTime()) : null;
        this.tssEksternIdFk = tssEksternIdFk;
        this.datoOpprettet = datoOpprettet != null ? new Timestamp(datoOpprettet.getTime()) : null;
        this.opprettetAv = opprettetAv;
        this.datoEndret = datoEndret != null ? new Timestamp(datoEndret.getTime()) : null;
        this.harUtlandPensj = harUtlandPensj;
        this.endretAv = endretAv;
        this.kKildeTpT = kKildeTpT;
        this.versjon = versjon;
        this.erGyldig = erGyldig;
        this.kSamtykkeSimT = kSamtykkeSimT;
        this.harSimulering = harSimulering;
        this.funkForholdId = funkForholdId;
        this.datoBrukFom = datoBrukFom != null ? new Timestamp(datoBrukFom.getTime()) : null;
        this.datoBrukTom = datoBrukTom != null ? new Timestamp(datoBrukTom.getTime()) : null;
    }

    public Date getDatoSamtykkeGitt() {
        return datoSamtykkeGitt != null ? new Date(datoSamtykkeGitt.getTime()) : null;
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
}
