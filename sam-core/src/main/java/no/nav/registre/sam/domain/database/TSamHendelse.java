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

import no.nav.registre.sam.domain.SyntetisertSamObject;
import no.nav.registre.sam.utils.Utils;

@Getter
@Setter
@ToString
@Entity(name = "t_sam_hendelse")
@AllArgsConstructor
public class TSamHendelse {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name = "p_seq", sequenceName = "S_SAM_HENDELSE", allocationSize = 1)
    @Column(name = "sam_hendelse_id")
    private int samHendelseId;
    @Column(name = "person_id")
    private int personId;
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
    @Column(name = "dato_endret")
    private Timestamp datoEndret;
    @Column(name = "endret_av")
    private String endretAv;
    @Column(name = "versjon")
    private int versjon;

    public TSamHendelse(SyntetisertSamObject obj, TPerson tPerson) throws ParseException {
        this.personId = tPerson.getPersonId();
        this.kTpArt = obj.getKTPArt();
        this.kSamHendelseT = obj.getKSamHendelseT();
        this.kKanalT = obj.getKKanalT();
        this.tssEksternIdFk = "80000470761";
        this.samMeldingIdFk = null;
        this.datoOpprettet = Utils.formatDate(obj.getDatoOpprettet());
        this.opprettetAv = (!"".equals(obj.getOpprettetAv())) ? obj.getOpprettetAv() : "synt";
        this.datoEndret = Utils.formatDate(obj.getDatoEndret());
        this.endretAv = "synt";
        this.versjon = (int) Double.parseDouble(obj.getVersjon());
    }
}
