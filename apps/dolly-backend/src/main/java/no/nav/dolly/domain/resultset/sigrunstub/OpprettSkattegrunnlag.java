package no.nav.dolly.domain.resultset.sigrunstub;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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