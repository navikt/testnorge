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
    private RsSigrunnOpprettSkattegrunnlag sigrunRequest;


    public RsDollyBestillingsRequest copy(){
        RsDollyBestillingsRequest request = new RsDollyBestillingsRequest();
        request.setEnvironments(this.environments);
        request.setAntall(this.antall);
        request.setTpsf(this.tpsf);
        request.setSigrunRequest(this.sigrunRequest);
        return request;
    }
}
