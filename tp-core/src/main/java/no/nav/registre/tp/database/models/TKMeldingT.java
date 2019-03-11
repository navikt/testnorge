package no.nav.registre.tp.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "T_K_MELDING_T")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TKMeldingT {

    @Id
    private String kMeldingT;

    private String dekode;
    private java.sql.Timestamp datoFom;
    private java.sql.Timestamp datoTom;
    private java.sql.Timestamp datoOpprettet;
    private String opprettetAv;
    private java.sql.Timestamp datoEndret;
    private String endretAv;
    private String erGyldig;

}
