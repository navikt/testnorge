package no.nav.registre.sam.domain.database;

import static no.nav.registre.sam.service.SyntetiseringService.ENDRET_OPPRETTET_AV;

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
import java.text.ParseException;

import no.nav.registre.sam.domain.SyntetisertSamordningsmelding;
import no.nav.registre.sam.utils.DateUtils;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "t_sam_vedtak")
public class TSamVedtak {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name = "p_seq", sequenceName = "S_SAM_VEDTAK", allocationSize = 1)
    @Column(name = "sam_vedtak_id")
    private Long samVedtakId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "k_fagomrade")
    private String kFagomraade;

    @Column(name = "k_vedtak_status")
    private String kVedtakStatus;

    @Column(name = "k_art")
    private String kArt;

    @Column(name = "fag_vedtak_id_fk")
    private Integer fagVedtakIdFk;

    @Column(name = "sak_id_fk")
    private Integer sakIdFk;

    @Column(name = "purring")
    private String purring;

    @Column(name = "etterbetaling")
    private String etterbetaling;

    @Column(name = "dato_fom")
    private Date datoFom;

    @Column(name = "dato_tom")
    private Date datoTom;

    @Column(name = "dato_opprettet")
    private Timestamp datoOpprettet;

    @Column(name = "opprettet_av")
    private String opprettetAv;

    @Column(name = "datoEndret")
    private Timestamp datoEndret;

    @Column(name = "endret_av")
    private String endretAv;

    @Column(name = "versjon")
    private int versjon;

    public TSamVedtak(SyntetisertSamordningsmelding obj, TPerson tPerson) throws ParseException {
        this.personId = tPerson.getPersonId();
        this.kFagomraade = obj.getKFagomraade();
        this.kVedtakStatus = obj.getKVedtakStatus();
        this.kArt = obj.getKArt();
        this.fagVedtakIdFk = 0;
        this.sakIdFk = Integer.parseInt(obj.getSakIdFk());
        this.purring = obj.getPurring();
        this.etterbetaling = obj.getEtterbetaling();
        this.datoFom = DateUtils.formatDate(obj.getDatoFom());
        this.datoTom = DateUtils.formatDate(obj.getDatoTom());
        this.datoOpprettet = DateUtils.formatTimestamp(obj.getDatoOpprettet());
        this.opprettetAv = "".equals(obj.getOpprettetAv()) ? ENDRET_OPPRETTET_AV : obj.getOpprettetAv();
        this.datoEndret = DateUtils.formatTimestamp(obj.getDatoEndret());
        this.endretAv = ENDRET_OPPRETTET_AV;
        this.versjon = (int) Double.parseDouble(obj.getVersjon());
    }

    public Date getDatoFom() {
        return new Date(datoFom.getTime());
    }

    public void setDatoFom(Date datoFom) {
        this.datoFom = new Date(datoFom.getTime());
    }

    public Date getDatoTom() {
        return new Date(datoTom.getTime());
    }

    public void setDatoTom(Date datoTom) {
        this.datoTom = new Date(datoTom.getTime());
    }

    public Timestamp getDatoOpprettet() {
        return new Timestamp(datoOpprettet.getTime());
    }

    public void setDatoOpprettet(Timestamp datoOpprettet) {
        this.datoOpprettet = new Timestamp(datoOpprettet.getTime());
    }

    public Timestamp getDatoEndret() {
        return new Timestamp(datoEndret.getTime());
    }

    public void setDatoEndret(Timestamp datoEndret) {
        this.datoEndret = new Timestamp(datoEndret.getTime());
    }
}
