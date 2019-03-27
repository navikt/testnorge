package no.nav.registre.sam.domain.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Entity(name = "t_sam_melding")
@AllArgsConstructor
@Builder
public class TSamMelding {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name = "p_seq", sequenceName = "S_SAM_MELDING", allocationSize = 1)
    @Column(name = "sam_melding_id")
    private int samMeldingId;
    @Column(name = "sam_vedtak_id")
    private int samVedtakId;
    @Column(name = "k_kanal_t")
    private String kKanalT;
    @Column(name = "k_melding_status")
    private String kMeldingStatus;
    @Column(name = "tss_ekstern_id_fk")
    private String tssEksternIdFk;
    @Column(name = "dato_sendt")
    private Date datoSendt;
    @Column(name = "dato_svart")
    private Date datoSvart;
    @Column(name = "dato_purret")
    private Date datoPurret;
    @Column(name = "refusjonskrav")
    private String refusjonskrav;
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
    @Column(name = "antall_forsok")
    private int antallForsoek;

    public TSamMelding(SyntetisertSamObject obj, TSamVedtak tSamVedtak) throws ParseException {
        this.samVedtakId = tSamVedtak.getSamVedtakId();
        this.kKanalT = obj.getKKanalT();
        this.kMeldingStatus = obj.getKMeldingStatus();
        this.tssEksternIdFk = obj.getTssEksternIdFk();
        this.datoSendt = Utils.formatDate(obj.getDatoSendt());
        this.datoSvart = Utils.formatDate(obj.getDatoSvart());
        this.datoPurret = Utils.formatDate(obj.getDatoPurret());
        this.refusjonskrav = obj.getRefusjonskrav();
        this.datoOpprettet = Utils.formatDate(obj.getDatoOpprettet());
        this.opprettetAv = ("".equals(obj.getOpprettetAv())) ? "synt" : obj.getOpprettetAv();
        this.datoEndret = Utils.formatDate(obj.getDatoEndret());
        this.endretAv = "synt";
        this.versjon = (int) Double.parseDouble(obj.getVersjon());
        this.antallForsoek = (int) Double.parseDouble(obj.getVersjon());

    }
}
