package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBasisMedSivilstandBestilling;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestillingLeggTilPaaGruppe extends RsDollyBestilling {

    private RsTpsfBasisMedSivilstandBestilling tpsf;

    private Boolean navSyntetiskIdent;
}