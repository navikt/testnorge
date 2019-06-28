package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsBrukerTeamAndGruppeIDs {
    List<RsTeamMedIdOgNavn> teams;
    List<String> favoritter;
    private String navIdent;
}