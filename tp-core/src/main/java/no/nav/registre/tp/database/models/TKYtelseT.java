package no.nav.registre.tp.database.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "T_K_YTELSE_T")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TKYtelseT {

    @Id
    private String kYtelseT;
    private String dekode;
    private java.sql.Date datoFom;
    private java.sql.Date datoTom;
    private java.sql.Timestamp datoOpprettet;
    private String opprettetAv;
    private java.sql.Timestamp datoEndret;
    private String endretAv;
    private String erGyldig;

}
