package no.nav.registre.sam.domain.database;

import lombok.*;
import no.nav.registre.sam.utils.Utils;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="t_person")
public class TPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_seq")
    @SequenceGenerator(name="p_seq", sequenceName = "S_PERSON", allocationSize=1)
    @Column(name="person_id")
    private int personId;
    @Column(name="fnr_fk")
    private String fnrFK;
    @Column(name="dato_opprettet")
    private Timestamp datoOpprettet;
    @Column(name="opprettet_av")
    private String opprettetAv;
    @Column(name="dato_endret")
    private Timestamp datoEndret;
    @Column(name="endret_av")
    private String endretAv;
    @Column(name="versjon")
    private int versjon;

    public TPerson(String fnrFK){
        this.fnrFK = fnrFK;
        this.datoOpprettet = Utils.getTodaysDate();
        this.opprettetAv = "synt";
        this.datoEndret = Utils.getTodaysDate();
        this.endretAv = "synt";
        this.versjon = 1;
    }
}
