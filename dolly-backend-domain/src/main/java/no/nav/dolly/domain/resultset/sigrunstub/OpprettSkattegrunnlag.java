package no.nav.dolly.domain.resultset.sigrunstub;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpprettSkattegrunnlag {

    public enum Tjeneste {BEREGNET_SKATT, SUMMERT_SKATTEGRUNNLAG}

    private List<KodeverknavnGrunnlag> grunnlag;
    private String inntektsaar;
    private String personidentifikator;
    private Boolean skjermet;
    private List<KodeverknavnGrunnlag> svalbardGrunnlag;
    private String testdataEier;
    private Tjeneste tjeneste;
}