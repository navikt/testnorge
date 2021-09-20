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
public class RsDollyUtvidetBestilling extends RsDollyBestilling {

    private RsTpsfUtvidetBestilling tpsf;

    private Boolean navSyntetiskIdent;

    private String beskrivelse;
}
