package no.nav.dolly.domain.resultSet;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RsDollyBestillingsRequest {

    private List<String> environments;

    private String identtype;
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private int antall;

    private boolean withAdresse;

    private RsAdresse boadresse;

    private List<RsPostadresse> postadresse;

    private Character kjonn;

    private String statsborgerskap;

    private String spesreg;

    private LocalDateTime spesregDato;

    private LocalDateTime doedsdato;

    private LocalDateTime regdato;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private String typeSikkerhetsTiltak;

    private LocalDateTime sikkerhetsTiltakDatoFom;

    private LocalDateTime sikkerhetsTiltakDatoTom;

    private String beskrSikkerhetsTiltak;

    /* Sigrunn */
    private RsSigrunnOpprettSkattegrunnlag sigrunRequest;
}
