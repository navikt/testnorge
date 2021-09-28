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
@Entity(name = "t_sam_melding")
public class TSamMelding {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name = "p_seq", sequenceName = "S_SAM_MELDING", allocationSize = 1)
    @Column(name = "sam_melding_id")
    private Long samMeldingId;

    @Column(name = "sam_vedtak_id")
    private Long samVedtakId;

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

    @Column(name = "datoEndret")
    private Timestamp datoEndret;

    @Column(name = "endret_av")
    private String endretAv;

    @Column(name = "versjon")
    private int versjon;

    @Column(name = "antall_forsok")
    private int antallForsoek;

    public TSamMelding(SyntetisertSamordningsmelding obj, TSamVedtak tSamVedtak) throws ParseException {
        this.samVedtakId = tSamVedtak.getSamVedtakId();
        this.kKanalT = obj.getKKanalT();
        this.kMeldingStatus = obj.getKMeldingStatus();
        this.tssEksternIdFk = obj.getTssEksternIdFk();
        this.datoSendt = DateUtils.formatDate(obj.getDatoSendt());
        this.datoSvart = DateUtils.formatDate(obj.getDatoSvart());
        this.datoPurret = DateUtils.formatDate(obj.getDatoPurret());
        this.refusjonskrav = obj.getRefusjonskrav();
        this.datoOpprettet = DateUtils.formatTimestamp(obj.getDatoOpprettet());
        this.opprettetAv = "".equals(obj.getOpprettetAv()) ? ENDRET_OPPRETTET_AV : obj.getOpprettetAv();
        this.datoEndret = DateUtils.formatTimestamp(obj.getDatoEndret());
        this.endretAv = ENDRET_OPPRETTET_AV;
        this.versjon = (int) Double.parseDouble(obj.getVersjon());
        this.antallForsoek = (int) Double.parseDouble(obj.getVersjon());
    }

    public Date getDatoSendt() {
        return new Date(datoSendt.getTime());
    }

    public void setDatoSendt(Date datoSendt) {
        this.datoSendt = new Date(datoSendt.getTime());
    }

    public Date getDatoSvart() {
        return new Date(datoSvart.getTime());
    }

    public void setDatoSvart(Date datoSvart) {
        this.datoSvart = new Date(datoSvart.getTime());
    }

    public Date getDatoPurret() {
        return new Date(datoPurret.getTime());
    }

    public void setDatoPurret(Date datoPurret) {
        this.datoPurret = new Date(datoPurret.getTime());
    }

    public Timestamp getDatoEndret() {
        return new Timestamp(datoEndret.getTime());
    }

    public void setDatoEndret(Timestamp datoEndret) {
        this.datoEndret = new Timestamp(datoEndret.getTime());
    }

    public Timestamp getDatoOpprettet() {
        return new Timestamp(datoOpprettet.getTime());
    }

    public void setDatoOpprettet(Timestamp datoOpprettet) {
        this.datoPurret = new Date(123L);
        this.datoOpprettet = new Timestamp(datoOpprettet.getTime());
    }
}
