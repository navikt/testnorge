package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsAdresse;
import no.nav.dolly.domain.resultset.RsPostadresse;

@Getter
@Setter
public class RsRelasjon {

    private String identtype;

    private String kjonn;

    private LocalDateTime foedtEtter;

    private LocalDateTime foedtFoer;

    private String sprakKode;

    private LocalDateTime datoSprak;

    private String spesreg;

    private LocalDateTime spesregDato;

    private String statsborgerskap;

    private LocalDateTime statsborgerskapRegdato;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private Boolean utenFastBopel;

    private RsAdresse boadresse;

    private List<RsPostadresse> postadresse;

    private String utvandretTilLand;

    private LocalDateTime utvandretTilLandFlyttedato;

    private Boolean harMellomnavn;

    private String innvandretFraLand;

    private LocalDateTime innvandretFraLandFlyttedato;
}
