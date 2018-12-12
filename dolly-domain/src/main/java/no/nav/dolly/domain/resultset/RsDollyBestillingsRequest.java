package no.nav.dolly.domain.resultset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsDollyBestillingsRequest {

    private List<String> environments;

    private int antall;

    private RsTpsfBestilling tpsf;

    private List<RsOpprettSkattegrunnlag> sigrunstub;

    private RsDigitalKontaktdata krrstub;
}