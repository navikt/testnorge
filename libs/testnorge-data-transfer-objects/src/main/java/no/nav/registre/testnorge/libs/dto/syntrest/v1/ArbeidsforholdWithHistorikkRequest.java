package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;


@Value
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class ArbeidsforholdWithHistorikkRequest extends ArbeidsforholdRequest {
    String historikk;

    public ArbeidsforholdWithHistorikkRequest(ArbeidsforholdRequest request, String historikk) {
        super(request);
        this.historikk = historikk;
    }
}
