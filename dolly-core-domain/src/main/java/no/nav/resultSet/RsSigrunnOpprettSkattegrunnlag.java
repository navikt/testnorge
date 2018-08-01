package no.nav.resultSet;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RsSigrunnOpprettSkattegrunnlag {

    private String testdataEier;
    private String personidentifikator;
    private String inntektsaar;
    private String tjeneste;
    private List<RsSigrunnKodeverknavnGrunnlag> grunnlag;
}
