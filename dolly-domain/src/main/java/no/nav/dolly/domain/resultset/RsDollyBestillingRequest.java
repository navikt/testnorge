package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestillingRequest extends RsDollyBestilling {

    private int antall;
    private RsTpsfUtvidetBestilling tpsf;
}