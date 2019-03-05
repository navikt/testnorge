package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestillingRequest extends RsDollyBestilling {

    private int antall;
    private RsTpsfUtvidetBestilling tpsf;
}