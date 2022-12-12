package no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPeriode;
import no.nav.dolly.domain.resultset.udistub.model.UdiHarType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsUdiArbeidsadgang {

    private UdiArbeidOmfangType arbeidsOmfang;
    private UdiHarType harArbeidsAdgang;
    private RsUdiPeriode periode;
    private UdiArbeidsadgangType typeArbeidsadgang;
    private String forklaring;
    private String hjemmel;
}
