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

import no.nav.registre.sam.utils.DateUtils;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "t_person")
public class TPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name = "p_seq", sequenceName = "S_PERSON", allocationSize = 1)
    @Column(name = "person_id")
    private Long personId;

    @Column(name = "fnr_fk")
    private String fnrFK;

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

    public TPerson(String fnrFK) {
        this.fnrFK = fnrFK;
        this.datoOpprettet = DateUtils.getTodaysDate();
        this.opprettetAv = ENDRET_OPPRETTET_AV;
        this.datoEndret = DateUtils.getTodaysDate();
        this.endretAv = ENDRET_OPPRETTET_AV;
        this.versjon = 1;
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
