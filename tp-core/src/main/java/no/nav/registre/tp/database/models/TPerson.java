package no.nav.registre.tp.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name = "T_PERSON")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TPerson {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(sequenceName = "T_PERSON_SEQ", name = "person_seq", allocationSize = 1)

    @Id
    private Integer personId;
    private String fnrFk;
    private java.sql.Timestamp datoOpprettet;
    private String opprettetAv;
    private java.sql.Timestamp datoEndret;
    private String endretAv;
    private String versjon;

}
