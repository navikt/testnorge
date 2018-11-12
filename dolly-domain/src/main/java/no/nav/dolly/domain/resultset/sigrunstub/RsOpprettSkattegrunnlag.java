package no.nav.dolly.domain.resultset.sigrunstub;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsOpprettSkattegrunnlag {

    private String testdataEier;
    private String personidentifikator;
    private String inntektsaar;
    private String tjeneste;
    private List<RsKodeverknavnGrunnlag> grunnlag;
}