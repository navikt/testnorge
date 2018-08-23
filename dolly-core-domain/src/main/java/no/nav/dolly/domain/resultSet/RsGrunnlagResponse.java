package no.nav.dolly.domain.resultSet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsGrunnlagResponse {

    private long id;
    private String personidentifikator;
    private String inntektsaar;
    private String tjeneste;
    private String grunnlag;
    private String verdi;
    private String testdataEier;
}
