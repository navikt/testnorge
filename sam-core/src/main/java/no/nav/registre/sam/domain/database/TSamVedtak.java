package no.nav.registre.sam.domain.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import no.nav.registre.sam.domain.SyntetisertSamObject;
import no.nav.registre.sam.utils.Utils;

@Getter
@Setter
@ToString
@Entity(name = "t_sam_vedtak")
@AllArgsConstructor
public class TSamVedtak {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name = "p_seq", sequenceName = "S_SAM_VEDTAK", allocationSize = 1)
    @Column(name = "sam_vedtak_id")
    private int samVedtakId;
    @Column(name = "person_id")
    private int personId;
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
    @Column(name = "dato_endret")
    private Timestamp dato_endret;
    @Column(name = "endret_av")
    private String endretAv;
    @Column(name = "versjon")
    private int versjon;

    public TSamVedtak(SyntetisertSamObject obj, TPerson tPerson) throws ParseException {
        this.personId = tPerson.getPersonId();
        this.kFagomraade = obj.getKFagomraade();
        this.kVedtakStatus = obj.getKVedtakStatus();
        this.kArt = obj.getKArt();
        this.fagVedtakIdFk = 0;
        this.sakIdFk = Integer.parseInt(obj.getSakIdFk());
        this.purring = obj.getPurring();
        this.etterbetaling = obj.getEtterbetaling();
        this.datoFom = Utils.formatDate(obj.getDatoFom());
        this.datoTom = Utils.formatDate(obj.getDatoTom());
        this.datoOpprettet = Utils.formatDate(obj.getDatoOpprettet());
        this.opprettetAv = ("".equals(obj.getOpprettetAv())) ? "synt" : obj.getOpprettetAv();
        this.dato_endret = Utils.formatDate(obj.getDatoEndret());
        this.endretAv = "synt";
        this.versjon = (int) Double.parseDouble(obj.getVersjon());
    }
}
