package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<RsMeldingStatusIdent> arenaforvalterStatus;
    private List<RsStatusMiljoeIdentForhold> aaregStatus;
    private RsPdlForvalterStatus pdlforvalterStatus;
}
