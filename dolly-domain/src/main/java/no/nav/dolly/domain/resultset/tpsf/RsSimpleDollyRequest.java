package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsSimpleDollyRequest {

    private String identtype;

    private Character kjonn;

    private LocalDateTime foedtEtter;

    private LocalDateTime foedtFoer;

    private int antall;

    private String sprakKode;

    private LocalDateTime datoSprak;

    private String spesreg;

    private LocalDateTime spesregDato;

    private String statsborgerskap;

    private LocalDateTime statsborgerskapRegdato;

    private LocalDateTime egenAnsattDatoFom;

    private LocalDateTime egenAnsattDatoTom;

    private Boolean utenFastBopel;
}
