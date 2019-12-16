package no.nav.dolly.domain.resultset.udistub.model.arbeidsadgang;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPeriode;
import no.nav.dolly.domain.resultset.udistub.model.UdiHarType;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class RsUdiArbeidsadgang {

    private UdiArbeidOmfangType arbeidsOmfang;
    private UdiHarType harArbeidsAdgang;
    private RsUdiPeriode periode;
    private UdiArbeidsadgangType typeArbeidsadgang;
}
