package no.nav.registre.tp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Ytelse {

    private Date datoInnmYtelFom;

    private String kYtelseT;

    private String meldingsType;
    private Date datoYtelIverFom;
    private Date datoYtelIverTom;
    private Timestamp datoOpprettet;
    private String opprettetAv;
    private Timestamp datoEndret;
    private String endretAv;
    private String versjon;
    private String erGyldig;

    private Timestamp datoBrukFom;
    private Timestamp datoBrukTom;

}
