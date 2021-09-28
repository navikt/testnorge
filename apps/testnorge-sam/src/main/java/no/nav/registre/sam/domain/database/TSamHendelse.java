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
import java.sql.Timestamp;
import java.text.ParseException;

import no.nav.registre.sam.domain.SyntetisertSamordningsmelding;
import no.nav.registre.sam.utils.DateUtils;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "t_sam_hendelse")
public class TSamHendelse {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name = "p_seq", sequenceName = "S_SAM_HENDELSE", allocationSize = 1)
    @Column(name = "sam_hendelse_id")
    private Long samHendelseId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "k_tp_art")
    private String kTpArt;

    @Column(name = "k_sam_hendelse_t")
    private String kSamHendelseT;

    @Column(name = "k_kanal_t")
    private String kKanalT;

    @Column(name = "tss_ekstern_id_fk")
    private String tssEksternIdFk;

    @Column(name = "sam_melding_id_fk")
    private String samMeldingIdFk;

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

    public TSamHendelse(SyntetisertSamordningsmelding obj, TPerson tPerson) throws ParseException {
        this.personId = tPerson.getPersonId();
        this.kTpArt = obj.getKTPArt();
        this.kSamHendelseT = obj.getKSamHendelseT();
        this.kKanalT = obj.getKKanalT();
        this.tssEksternIdFk = obj.getTssEksternIdFk();
        this.samMeldingIdFk = null;
        this.datoOpprettet = DateUtils.formatTimestamp(obj.getDatoOpprettet());
        this.opprettetAv = !"".equals(obj.getOpprettetAv()) ? obj.getOpprettetAv() : ENDRET_OPPRETTET_AV;
        this.datoEndret = DateUtils.formatTimestamp(obj.getDatoEndret());
        this.endretAv = ENDRET_OPPRETTET_AV;
        this.versjon = (int) Double.parseDouble(obj.getVersjon());
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
