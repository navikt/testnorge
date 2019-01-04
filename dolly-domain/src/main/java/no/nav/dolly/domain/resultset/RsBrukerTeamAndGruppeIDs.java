package no.nav.dolly.domain.resultset;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsBrukerTeamAndGruppeIDs {
    private String navIdent;
    List<RsTeamMedIdOgNavn> teams;
    List<String> favoritter;
}