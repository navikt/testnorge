package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsAdresse;
import no.nav.dolly.domain.resultset.RsPostadresse;

@Getter
@Setter
public class RsTpsfBestilling {

    private List<String> environments;

    private int antall;

    private RsSimpleRelasjoner relasjoner;

    private String identtype;

    private LocalDateTime foedtEtter;

    private LocalDateTime foedtFoer;

    private boolean withAdresse;

    private RsAdresse boadresse;

    private List<RsPostadresse> postadresse;

    private String kjonn;

    private String statsborgerskap;

    private LocalDateTime statsborgerskapRegdato;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private String sivilstand;

    private LocalDateTime regdato;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private String typeSikkerhetsTiltak;

    private LocalDateTime sikkerhetsTiltakDatoFom;

    private LocalDateTime sikkerhetsTiltakDatoTom;

    private String beskrSikkerhetsTiltak;

    private String sprakKode;

    private LocalDateTime datoSprak;
}
