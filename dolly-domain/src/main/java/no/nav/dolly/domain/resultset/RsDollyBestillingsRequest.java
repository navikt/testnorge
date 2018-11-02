package no.nav.dolly.domain.resultset;

import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;

import java.util.List;

@Getter
@Setter
public class RsDollyBestillingsRequest {

    private List<String> environments;

    private int antall;

    private RsTpsfBestilling tpsf;

    /* Sigrunn */
    private List<RsSigrunnOpprettSkattegrunnlag> sigrunRequest;

}
