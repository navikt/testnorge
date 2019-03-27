package no.nav.registre.sam.domain.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.sql.Timestamp;

import no.nav.registre.sam.utils.Utils;

@Getter
@Setter
@ToString
@Entity(name = "t_person")
@AllArgsConstructor
@NoArgsConstructor
public class TPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name = "p_seq", sequenceName = "S_PERSON", allocationSize = 1)
    @Column(name = "person_id")
    private int personId;
    @Column(name = "fnr_fk")
    private String fnrFK;
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

    public TPerson(String fnrFK) {
        this.fnrFK = fnrFK;
        this.datoOpprettet = Utils.getTodaysDate();
        this.opprettetAv = "synt";
        this.datoEndret = Utils.getTodaysDate();
        this.endretAv = "synt";
        this.versjon = 1;
    }
}
