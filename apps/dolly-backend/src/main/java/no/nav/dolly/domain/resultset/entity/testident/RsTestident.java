package no.nav.dolly.domain.resultset.entity.testident;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.arenaforvalter.RsMeldingStatusIdent;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdlForvalterStatus;
import no.nav.dolly.domain.resultset.RsStatusMiljoeIdentForhold;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTestident {

    private String ident;
    private String tpsfSuccessEnv;
    private String krrstubStatus;
    private String sigrunstubStatus;
    private String udistubStatus;
    private List<RsMeldingStatusIdent> arenaforvalterStatus;
    private List<RsStatusMiljoeIdentForhold> aaregStatus;
    private RsPdlForvalterStatus pdlforvalterStatus;
    private List<RsStatusMiljoeIdentForhold> instdataStatus;
}
