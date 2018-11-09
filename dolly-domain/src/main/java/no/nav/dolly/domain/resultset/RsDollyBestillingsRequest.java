package no.nav.dolly.domain.resultset;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;

@Getter
@Setter
public class RsDollyBestillingsRequest {

    private List<String> environments;

    private int antall;

    private RsTpsfBestilling tpsf;

    private List<RsOpprettSkattegrunnlag> sigrunstub;

    private RsDigitalKontaktdata krrstub;
}