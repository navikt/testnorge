package no.nav.dolly.bestilling.sigrunstub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.dolly.domain.resultset.sigrunstub.KodeverknavnGrunnlag;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SigrunstubLignetInntektRequest extends SigrunstubRequest{

    public enum Tjeneste {BEREGNET_SKATT, SUMMERT_SKATTEGRUNNLAG}

    private List<KodeverknavnGrunnlag> grunnlag;

    private String personidentifikator;
    private Boolean skjermet;
    private List<KodeverknavnGrunnlag> svalbardGrunnlag;

    private Tjeneste tjeneste;

    public List<KodeverknavnGrunnlag> getGrunnlag() {
        if (isNull(grunnlag)) {
            grunnlag = new ArrayList<>();
        }
        return grunnlag;
    }

    public List<KodeverknavnGrunnlag> getSvalbardGrunnlag() {
        if (isNull(svalbardGrunnlag)) {
            svalbardGrunnlag = new ArrayList<>();
        }
        return svalbardGrunnlag;
    }
}