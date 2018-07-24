package no.nav.appserivces.tpsf.domain.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RsSigrunStubRequest {

    @NotBlank
    private String personidentifikator;

    private String grunnlag;

    private String inntektsaar;

    private String tjeneste;

    private String testdataEier;

    private String verdi;
}
