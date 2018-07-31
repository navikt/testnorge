package no.nav.appserivces.sigrunstub.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RsSigrunnOpprettSkattegrunnlag {

    private String personidentifikator;
    private String inntektsaar;
    private String tjeneste;
    private List<RsSigrunnGrunnlag> grunnlag;
}
